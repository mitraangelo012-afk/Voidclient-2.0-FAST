#ifndef VOIDCLIENT_NATIVE_CLIENT_H
#define VOIDCLIENT_NATIVE_CLIENT_H

#include <jni.h>
#include <unistd.h>
#include <sys/uio.h>
#include <vector>
#include <string>
#include <map>

// Entity structure
struct Entity {
    uint64_t address;
    float x, y, z;
    float health;
    std::string name;
    bool isPlayer;
};

// Client class
class NativeClient {
public:
    static NativeClient& getInstance() {
        static NativeClient instance;
        return instance;
    }

    bool initialize();
    bool cleanup();
    bool update();

    bool attachToProcess(pid_t pid);
    std::vector<char> readMemory(uint64_t address, size_t size);
    bool writeMemory(uint64_t address, const void* data, size_t size);

    std::vector<Entity> getEntityList();
    Entity getLocalPlayer();

private:
    NativeClient() = default;
    ~NativeClient() = default;

    // Disable copy constructor and assignment operator
    NativeClient(const NativeClient&) = delete;
    NativeClient& operator=(const NativeClient&) = delete;

    pid_t targetPid = -1;
    bool attached = false;

    // Helper functions
    bool findMinecraftProcess();
    uint64_t findBaseAddress();
    std::vector<uint64_t> findEntityList();
    Entity readEntityData(uint64_t entityAddr);

    // Memory utilities
    ssize_t processVmReadv(pid_t pid, const struct iovec* local_iov,
                          unsigned long liovcnt, const struct iovec* remote_iov,
                          unsigned long riovcnt, unsigned long flags);
    ssize_t processVmWritev(pid_t pid, const struct iovec* local_iov,
                           unsigned long liovcnt, const struct iovec* remote_iov,
                           unsigned long riovcnt, unsigned long flags);
};

// JNI helper functions
extern "C" {
    JNIEXPORT jboolean JNICALL
    Java_com_voidclient_native_NativeInterface_nativeInit(JNIEnv *env, jobject thiz);

    JNIEXPORT jboolean JNICALL
    Java_com_voidclient_native_NativeInterface_nativeCleanup(JNIEnv *env, jobject thiz);

    JNIEXPORT jboolean JNICALL
    Java_com_voidclient_native_NativeInterface_nativeUpdate(JNIEnv *env, jobject thiz);

    JNIEXPORT jboolean JNICALL
    Java_com_voidclient_native_NativeInterface_attachToProcess(JNIEnv *env, jobject thiz, jint pid);

    JNIEXPORT jbyteArray JNICALL
    Java_com_voidclient_native_NativeInterface_readMemory(JNIEnv *env, jobject thiz, jlong address, jint size);

    JNIEXPORT jboolean JNICALL
    Java_com_voidclient_native_NativeInterface_writeMemory(JNIEnv *env, jobject thiz, jlong address, jbyteArray data);

    JNIEXPORT jlong JNICALL
    Java_com_voidclient_native_NativeInterface_getEntityList(JNIEnv *env, jobject thiz);

    JNIEXPORT jlong JNICALL
    Java_com_voidclient_native_NativeInterface_getLocalPlayer(JNIEnv *env, jobject thiz);
}

#endif //VOIDCLIENT_NATIVE_CLIENT_H