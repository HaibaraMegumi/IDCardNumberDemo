# IDCardNumberDemo
## 身份证号码识别
需要用到 [OpenCV](https://opencv.org/) 和 [tess-two](https://github.com/rmtheis/tess-two)
OCR 训练数据 [ck.traineddata](https://github.com/wen704/SimpleMultiAdapterDemo/blob/master/library/src/main/assets/ck.traineddata) 识别准确率相对比 [num.traineddata](https://github.com/wen704/IDCardNumberDemo/blob/master/app/src/main/assets/num.traineddata) 高

### 识别过程
1. 身份证图片处理
 1. 归一化 resize
 2. 灰度化 cvtColor
 3. 二值化 threshold
 4. 膨胀化 erode
 5. 轮廓检测 findContours
 6. 裁剪
2. OCR 识别 TessBaseAPI.setImage TessBaseAPI.getUTF8Text

整体过程都处于调用 OpenCV 和 tess-two 的 api 方法,具体步骤参考代码


