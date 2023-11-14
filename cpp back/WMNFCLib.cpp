#include <jni.h>
#include <string>
#include "aesmodded.h"
#define SUCCESS									0x00

extern "C" [[maybe_unused]] JNIEXPORT jstring JNICALL
        gt_nfc_wmlib_getNativeString(JNIEnv* env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

uint8_t aesDefaultKey[16] = { 0x03, 0xE3, 0xF1, 0x77, 0x01, 0x00, 0xC3, 0x0B,
                                 0x27, 0x68, 0x49, 0x20, 0x00, 0xFF, 0xE5, 0x0C };

uint8_t aesInitVector[16] = { 0x08, 0x42, 0x00, 0x6F, 0x8E, 0x03, 0x33, 0xBC,
                                           0xF6, 0x91, 0x05, 0x43, 0x00, 0xEC ,   0x0A , 0x00 };


uint8_t  CommandDataConnect[15]  = { 0x00, 0xA4, 0x04, 0x00, 0x09, 0xAE, 0x02, 0x00, 0x00, 0x00,
                            0x00, 0x02, 0x00, 0x00, 0x00     };

uint16_t EncryptBuffer(uint8_t* inputbuffer, uint8_t* WritingBuf,   uint16_t datasize)
{
    AES_ctx ctx;
    memcpy(ctx.RoundKey, aesDefaultKey, 0x10);
    memcpy(ctx.Iv, aesInitVector, 0x10);
    AES_init_ctx_iv(&ctx, aesDefaultKey, aesInitVector);
    AES_CBC_encrypt_buffer(&ctx, inputbuffer, datasize);
    memcpy(WritingBuf, inputbuffer, datasize);

    //	AES_CBC_decrypt_buffer(&ctx, inputbuffer, 64);

    return SUCCESS;
}


uint16_t DecryptBuffer(uint8_t* inputbuffer, uint8_t* WritingBuf,  uint16_t datasize)
{
    AES_ctx ctx;
    memcpy(ctx.RoundKey, aesDefaultKey, 0x10);
    memcpy(ctx.Iv, aesInitVector, 0x10);
    AES_init_ctx_iv(&ctx, aesDefaultKey, aesInitVector);
    //AES_CBC_encrypt_buffer(&ctx, inputbuffer, datasize);

    AES_CBC_decrypt_buffer(&ctx, inputbuffer, datasize);
    memcpy(WritingBuf, inputbuffer, datasize);

    return SUCCESS;
}



extern "C"
JNIEXPORT jint JNICALL
Java_gt_nfc_wmlib_GTNFCWaterMeterClass_EncryptBuffer(JNIEnv *env, jobject thiz,
                                                     jbyteArray inputbuffer, jbyteArray WritingBuf,
                                                     jint datasize) {
    // TODO: implement EncryptBuffer()

    AES_ctx ctx;
    memcpy(ctx.RoundKey, aesDefaultKey, 0x10);
    memcpy(ctx.Iv, aesInitVector, 0x10);
    AES_init_ctx_iv(&ctx, aesDefaultKey, aesInitVector);
    AES_CBC_encrypt_buffer(&ctx, reinterpret_cast<uint8_t *>(inputbuffer), datasize);
    memcpy(WritingBuf, inputbuffer, datasize);

    //	AES_CBC_decrypt_buffer(&ctx, inputbuffer, 64);

    return SUCCESS;

}


extern "C"
JNIEXPORT jbyteArray  JNICALL
Java_gt_nfc_wmlib_GTNFCWaterMeterClass_GetReadingCommand(JNIEnv *env, jobject thiz, jbyte index) {
    // TODO: implement GetReadingCommand33()
    uint8_t Command[5] = { 0, 0 , 0 , 0 , 0    } ;
    jbyteArray result = env->NewByteArray(5);
    if(index == 0 )
    {
        jbyteArray result2 = env->NewByteArray(15);

        env->SetByteArrayRegion(result2, 0, 15, reinterpret_cast<const jbyte*>(CommandDataConnect));
        return  result2 ;
    }
    else if(index > 2)
    {
        Command[1] = 0x55 ;
        index = index - 3 ;
    } else
    {
        Command[1] = 0x44 ;
        index = index - 1 ;
    }

    uint16_t pos = index * 0xFA ;
    Command[2] = pos << 8 ;
    Command[3] = (uint8_t) pos   ;
    Command[4] = 0xFA   ;
    char str[(sizeof Command) + 1];

    env->SetByteArrayRegion(result, 0, 5, reinterpret_cast<const jbyte*>(Command));

    return result;



}