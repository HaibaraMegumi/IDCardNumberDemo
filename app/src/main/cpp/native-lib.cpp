#define LOG_TAG "OpenCV"

#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include "utils.h"

#define DEFAULT_CARD_WIDTH 640
#define DEFAULT_CARD_HEIGHT 400
#define FIX_IDCARD_SIZE Size(DEFAULT_CARD_WIDTH,DEFAULT_CARD_HEIGHT)
using namespace std;
using namespace cv;

extern "C" JNIEXPORT jstring

JNICALL
Java_com_gaofu_idcardnumberdemo_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jobject

JNICALL
Java_com_gaofu_idcardnumberdemo_MainActivity_findIdNumber(JNIEnv *env, jobject thiz, jobject bitmap,
                                                          jobject argb8888) {
    // 1.将 Bitmap 转成矩阵
    Mat src_img;
    Mat dst_img;
    bitmap2Mat(env, bitmap, &src_img);
    // 归一化
    Mat dst;
    resize(src_img, dst, FIX_IDCARD_SIZE);
    // 灰度化
    cvtColor(src_img, dst, COLOR_RGB2GRAY);
    // 二值化
    threshold(dst, dst, 110, 255, THRESH_BINARY);
    // 膨胀处理
    Mat erodeElement = getStructuringElement(MORPH_RECT, Size(45, 10));
    erode(dst, dst, erodeElement);
    // 轮廓检测
    vector <Rect> rects;
    vector <vector<Point>> contours;
    findContours(dst, contours, RETR_TREE, CHAIN_APPROX_SIMPLE, Point(0, 0));
    // 逻辑处理得到最终的轮廓
    for (int i = 0; i < contours.size(); ++i) {
        // 获取矩形
        Rect rect = boundingRect(contours.at(i));
        // 绘制
//        rectangle(dst, rect, Scalar(0, 0, 255));
        // 根据号码规则去判断
        if (rect.width > rect.height * 8 && rect.width < rect.height * 16) {
            // 需要的区域
            rects.push_back(rect);
        }
    }
    // 获取最终区域
    int lowPoint = 0;
    Rect finalRect;
    for (int i = 0; i < rects.size(); ++i) {
        Rect rect = rects.at(i);
        Point point = rect.tl();
        if (point.y > lowPoint) {
            lowPoint = point.y;
            finalRect = rect;
        }
    }
    // 去裁剪
    dst_img = src_img(finalRect);
    // 回收
//    free(&dst);
    // ...
    // 2.将矩阵转回 Bitmap
    return createBitmap(env, dst_img, argb8888);
}