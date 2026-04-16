#include "native_client.h"
#include <android/log.h>
#include <sys/syscall.h>
#include <fcntl.h>
#include <dirent.h>
#include <cstring>
#include <cstdlib>

#define LOG_TAG "VoidClient-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Implementation of NativeClient methods
bool NativeClient::initialize() {
    LOGI("Initializing NativeClient");

    // Find and attach to Minecraft process
    if (!findMinecraftProcess()) {
        LOGE("Failed to find Minecraft process");
        return false;
    }

    attached = true;
    LOGI("NativeClient initialized successfully");
    return true;
}

bool NativeClient::cleanup() {
    LOGI("Cleaning up NativeClient");
    attached = false;
    targetPid = -1;
    return true;
}

bool NativeClient::update() {
    if (!attached) {
        LOGE("Not attached to process");
        return false;
    }

    // Perform periodic updates
    // This is where we would update ESP, aim assist, etc.
    return true;
}

bool NativeClient::attachToProcess(pid_t pid) {
    LOGI("Attaching to process %d", pid);
    targetPid = pid;
    attached = true;
    return true;
}

std::vector<char> NativeClient::readMemory(uint64_t address, size_t size) {
    std::vector<char> buffer(size);

    struct iovec local_iov = {buffer.data(), size};
    struct iovec remote_iov = {(void*)address, size};

    ssize_t result = processVmReadv(targetPid, &local_iov, 1, &remote_iov, 1, 0);

    if (result == -1) {
        LOGE("Failed to read memory at 0x%llx", address);
        return {};
    }

    return buffer;
}

bool NativeClient::writeMemory(uint64_t address, const void* data, size_t size) {
    struct iovec local_iov = {const_cast<void*>(data), size};
    struct iovec remote_iov = {(void*)address, size};

    ssize_t result = processVmWritev(targetPid, &local_iov, 1, &remote_iov, 1, 0);

    if (result == -1) {
        LOGE("Failed to write memory at 0x%llx", address);
        return false;
    }

    return true;
}

std::vector<Entity> NativeClient::getEntityList() {
    std::vector<Entity> entities;

    // This would normally scan for entities in the game
    // For now, we'll return an empty list
    return entities;
}

Entity NativeClient::getLocalPlayer() {
    Entity player = {};
    player.address = 0;
    player.x = 0.0f;
    player.y = 0.0f;
    player.z = 0.0f;
    player.health = 0.0f;
    player.name = "LocalPlayer";
    player.isPlayer = true;
    return player;
}

bool NativeClient::findMinecraftProcess() {
    DIR* dir = opendir("/proc");
    if (!dir) {
        LOGE("Failed to open /proc directory");
        return false;
    }

    struct dirent* entry;
    while ((entry = readdir(dir)) != nullptr) {
        // Skip non-numeric entries
        if (!isdigit(entry->d_name[0])) {
            continue;
        }

        // Construct path to cmdline file
        char path[256];
        snprintf(path, sizeof(path), "/proc/%s/cmdline", entry->d_name);

        // Open cmdline file
        FILE* file = fopen(path, "r");
        if (!file) {
            continue;
        }

        // Read command line
        char cmdline[1024];
        if (fgets(cmdline, sizeof(cmdline), file)) {
            // Check if this is the Minecraft process
            if (strstr(cmdline, "com.mojang.minecraftpe")) {
                targetPid = atoi(entry->d_name);
                fclose(file);
                closedir(dir);
                LOGI("Found Minecraft process with PID: %d", targetPid);
                return true;
            }
        }

        fclose(file);
    }

    closedir(dir);
    LOGE("Minecraft process not found");
    return false;
}

uint64_t NativeClient::findBaseAddress() {
    // This would normally find the base address of the Minecraft module
    // For now, we'll return 0
    return 0;
}

std::vector<uint64_t> NativeClient::findEntityList() {
    // This would normally scan memory for entity list pointers
    // For now, we'll return an empty vector
    return {};
}

Entity NativeClient::readEntityData(uint64_t entityAddr) {
    Entity entity = {};
    entity.address = entityAddr;
    // This would normally read entity data from memory
    // For now, we'll return a default entity
    return entity;
}

ssize_t NativeClient::processVmReadv(pid_t pid, const struct iovec* local_iov,
                                    unsigned long liovcnt, const struct iovec* remote_iov,
                                    unsigned long riovcnt, unsigned long flags) {
    return syscall(__NR_process_vm_readv, pid, local_iov, liovcnt, remote_iov, riovcnt, flags);
}

ssize_t NativeClient::processVmWritev(pid_t pid, const struct iovec* local_iov,
                                     unsigned long liovcnt, const struct iovec* remote_iov,
                                     unsigned long riovcnt, unsigned long flags) {
    return syscall(__NR_process_vm_writev, pid, local_iov, liovcnt, remote_iov, riovcnt, flags);
}

// JNI Implementation
extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_voidclient_native_NativeInterface_nativeInit(JNIEnv *env, jobject thiz) {
    return NativeClient::getInstance().initialize();
}

JNIEXPORT jboolean JNICALL
Java_com_voidclient_native_NativeInterface_nativeCleanup(JNIEnv *env, jobject thiz) {
    return NativeClient::getInstance().cleanup();
}

JNIEXPORT jboolean JNICALL
Java_com_voidclient_native_NativeInterface_nativeUpdate(JNIEnv *env, jobject thiz) {
    return NativeClient::getInstance().update();
}

JNIEXPORT jboolean JNICALL
Java_com_voidclient_native_NativeInterface_attachToProcess(JNIEnv *env, jobject thiz, jint pid) {
    return NativeClient::getInstance().attachToProcess(pid);
}

JNIEXPORT jbyteArray JNICALL
Java_com_voidclient_native_NativeInterface_readMemory(JNIEnv *env, jobject thiz, jlong address, jint size) {
    auto data = NativeClient::getInstance().readMemory(address, size);

    if (data.empty()) {
        return nullptr;
    }

    jbyteArray result = env->NewByteArray(data.size());
    if (result) {
        env->SetByteArrayRegion(result, 0, data.size(), reinterpret_cast<const jbyte*>(data.data()));
    }

    return result;
}

JNIEXPORT jboolean JNICALL
Java_com_voidclient_native_NativeInterface_writeMemory(JNIEnv *env, jobject thiz, jlong address, jbyteArray data) {
    jsize dataSize = env->GetArrayLength(data);
    jbyte* buffer = env->GetByteArrayElements(data, nullptr);

    bool result = NativeClient::getInstance().writeMemory(address, buffer, dataSize);

    env->ReleaseByteArrayElements(data, buffer, JNI_ABORT);
    return result;
}

JNIEXPORT jlong JNICALL
Java_com_voidclient_native_NativeInterface_getEntityList(JNIEnv *env, jobject thiz) {
    // Return pointer to entity list (as jlong)
    auto entities = NativeClient::getInstance().getEntityList();
    return reinterpret_cast<jlong>(new std::vector<Entity>(entities));
}

JNIEXPORT jlong JNICALL
Java_com_voidclient_native_NativeInterface_getLocalPlayer(JNIEnv *env, jobject thiz) {
    // Return pointer to local player (as jlong)
    Entity player = NativeClient::getInstance().getLocalPlayer();
    return reinterpret_cast<jlong>(new Entity(player));
}

}