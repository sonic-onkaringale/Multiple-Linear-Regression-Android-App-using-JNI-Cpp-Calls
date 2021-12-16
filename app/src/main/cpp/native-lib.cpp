#include <jni.h>
#include "Matrix.h"
#include <vector>
#include "LinearReg.h"
#include <string>
using namespace std;
using namespace matrix;
extern "C" JNIEXPORT jstring JNICALL



Java_com_example_cpp_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */)
{
    std::string hello = "Hello from C++";


    Matrix<long double> print (3,3,1);
    string p;
    for (int i = 0; i < print.rows(); ++i) {
        for (int j = 0; j < print.cols(); ++j) {
            p += " " + to_string(print[i][j]);
        }
        p+= "\n";
    }

    return env->NewStringUTF(p.c_str());
}


extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_cpp_csv_1dashboard_stringarrayfromJNI(JNIEnv *env,jobject thiz,jint rows,jint cols,jobject jdataset) {
    // TODO: implement stringarrayfromJNI()
    vector<string> dataset;
    jclass cList = env->FindClass("java/util/List");
    jmethodID mSize = env->GetMethodID(cList, "size", "()I");
    jmethodID mGet = env->GetMethodID(cList, "get", "(I)Ljava/lang/Object;");
    jint size = env->CallIntMethod(jdataset, mSize);
    for (jint i = 0; i < size; ++i)
    {
        jstring strObj = (jstring)env->CallObjectMethod(jdataset, mGet, i);
        const char * chr = env->GetStringUTFChars(strObj, NULL);
        dataset.emplace_back(chr);
        env->ReleaseStringUTFChars(strObj, chr);
    }
    vector<string> headers;
    vector<vector<long double>> forward_dataset;
    forward_dataset.resize(rows -1);
    for (int i = 0; i < forward_dataset.size(); ++i)
    {
        forward_dataset[i].resize(cols);
    }
    headers.resize(cols);
    for (int i = 0; i < cols; ++i)
    {
        headers[i] = dataset[i];
    }
    int iter = cols;
    for (int j = 0; j < forward_dataset.size(); ++j)
    {
        for (int k = 0; k < forward_dataset[j].size(); ++k)
        {

            forward_dataset[j][k] = stold(dataset[iter]);
            ++iter;
        }
    }
    dataset::Dataset ds(headers,forward_dataset);
    linearreg::LinearReg linearReg(ds);
    vector<string> output;
    output.push_back(linearReg.r2_score());
    Matrix<long double> beta = linearReg.beta();
    for (int i = 0; i < beta.rows(); ++i)
    {
        for (int j = 0; j < beta.cols(); ++j)
        {
            output.push_back(to_string(beta[i][j]));
        }
    }
    jobjectArray result;
    result = (jobjectArray)env->NewObjectArray(output.size(),env->FindClass("java/lang/String"),env->NewStringUTF(""));
    for(int i=0; i<output.size(); i++)
    {
        env->SetObjectArrayElement(result,i,env->NewStringUTF(output[i].c_str()));
    }

    return result;
}