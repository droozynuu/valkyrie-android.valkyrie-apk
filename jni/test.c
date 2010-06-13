#include <jni.h>
#include <android/log.h>




JNIEXPORT jint JNICALL 
Java_com_firegnom_valkyrie_util_NativeUnzip_macio123(JNIEnv*  env,jstring fileName,jstring dest){
	(void)env;
	jboolean iscopy;
	__android_log_print(ANDROID_LOG_INFO, "native macio", "jestem1");
	const jchar *c_str = (*env)->GetStringCritical(env,fileName, 0);
	__android_log_print(ANDROID_LOG_INFO, "native macio", "alaaaa1");
	__android_log_print(ANDROID_LOG_INFO, "native macio", "ala1");
	
	__android_log_print(ANDROID_LOG_INFO, "native macio", "jestem2 %s",c_str);
	const jchar *dir = (*env)->GetStringCritical(env, dest, 0);
	(*env)->ReleaseStringCritical(env, fileName, c_str);
	(*env)->ReleaseStringCritical(env, dest, dir);
	return 0;
}
