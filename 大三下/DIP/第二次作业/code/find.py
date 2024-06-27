import cv2
import numpy as np


def detect(image_path):
    origin_image = cv2.imread(image_path)
    # 灰度图
    gray = cv2.cvtColor(origin_image, cv2.COLOR_BGR2GRAY)
    # 分辨率调整 高斯去噪
    blurred = cv2.GaussianBlur(gray, (5, 5), 0, 0, cv2.BORDER_DEFAULT)
    # sobel算子边缘检测
    Sobel_x = cv2.Sobel(blurred, cv2.CV_16S, 1, 0)
    absX = cv2.convertScaleAbs(Sobel_x)  # 转回uint8
    image = absX
    # 自适应阈值处理--—获得二值化图
    ret, image = cv2.threshold(image, 0, 255, cv2.THRESH_OTSU)

    # 闭运算，白色部分练成整体
    kernelX = cv2.getStructuringElement(cv2.MORPH_RECT, (15, 5))
    image = cv2.morphologyEx(image, cv2.MORPH_CLOSE, kernelX, iterations=1)
    # 去除小白点
    kernelX = cv2.getStructuringElement(cv2.MORPH_RECT, (50, 1))
    kernelY = cv2.getStructuringElement(cv2.MORPH_RECT, (1, 20))

    # x方向进行闭操作（抑制暗细节）
    image = cv2.dilate(image, kernelX)  # 膨胀
    image = cv2.erode(image, kernelX)  # 腐蚀

    # y方向的开操作
    image = cv2.erode(image, kernelY)  # 腐蚀
    image = cv2.dilate(image, kernelY)  # 膨胀

    # 中值滤波去除噪点
    edge2 = cv2.medianBlur(image, 15)
    cv2.imshow('edge',edge2)

    # 查找图像边缘整体形成的矩形区域，可能有很多，车牌就在其中一个矩形区域中
    contours, hierarchy = cv2.findContours(edge2, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    # 将轮廓规整为长方形
    rectangles = []
    for c in contours:
        x = []
        y = []
        for point in c:
            y.append(point[0][0])
            x.append(point[0][1])
        r = [min(y), min(x), max(y), max(x)]
        rectangles.append(r)

    # 排除形状不对的
    car_contours =[]
    for r in rectangles:
        area_width = r[2]-r[0]
        area_height = r[3]-r[1]
        if area_width < area_height:
            area_width, area_height = area_height, area_width
        wh_ratio = area_width / area_height
        # print(wh_ratio)
        # 要求矩形区域长宽比在2到6之间，2到6是车牌的长宽比，其余的矩形排除
        if wh_ratio > 2 and wh_ratio < 6:
            car_contours.append(r)


    # 用颜色识别出车牌区域
    dist_r = []
    max_mean = 0
    for r in car_contours:

        block = origin_image[r[1]:r[3], r[0]:r[2]]
        hsv = cv2.cvtColor(block, cv2.COLOR_BGR2HSV)

        for i in range(3):
            if i == 0: # 蓝色
                low = np.array([100, 55, 55])
                up = np.array([125, 255, 255])

            if i == 1: # 黄色
                low = np.array([20, 55, 55])
                up = np.array([40, 255, 255])

            if i == 2: # 绿色
                low = np.array([35, 55, 55])
                up = np.array([80, 255, 255])

            result = cv2.inRange(hsv, low, up)
            # 用计算均值的方式找出符合指定颜色的区域
            mean = cv2.mean(result)
            if mean[0] > max_mean:
                color = i
                # print(i, r[0])
                max_mean = mean[0]
                dist_r = r


   # print(dist_r[0],dist_r[1] , dist_r[2] ,dist_r[3])
    res = origin_image[dist_r[1]:dist_r[3], dist_r[0]:dist_r[2]]

    cv2.imshow("img", res)
    return res,color