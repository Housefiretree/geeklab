import cv2
import numpy as np
from matplotlib import pyplot as plt
import os

# 将可能歪斜的车牌校正
def correct_plate_orientation(image):
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    edges = cv2.Canny(gray, 50, 150)
    lines = cv2.HoughLines(edges, 1, np.pi/180, 100)

    if lines is not None:
        #print(lines[0])
        for rho, theta in lines[0]:
            a = np.cos(theta)
            b = np.sin(theta)
            x0 = a * rho
            y0 = b * rho
            pt1 = (int(x0 + 1000*(-1)*b), int(y0 + 1000*a))
            pt2 = (int(x0 - 1000*(-1)*b), int(y0 - 1000*a))
            angle = np.arctan2(pt2[1] - pt1[1], pt2[0] - pt1[0]) * 180.0 / np.pi

        rotated_image = image.copy()
        (h, w) = image.shape[:2]
        center = (w // 2, h // 2)
        M = cv2.getRotationMatrix2D(center, angle, 1.0)
        rotated_image = cv2.warpAffine(rotated_image, M, (w, h), flags=cv2.INTER_CUBIC, borderMode=cv2.BORDER_REPLICATE)

        return rotated_image
    return image

#车牌字符分割
def split_char(image,color):
    # 图像去噪
    image_denoise = cv2.GaussianBlur(image, (3, 3), 0)
    # 将可能歪斜的车牌校正
    image_corrected = correct_plate_orientation(image_denoise)
    # 获得灰度图
    image_gray = cv2.cvtColor(image_corrected, cv2.COLOR_RGB2GRAY)
    # 获得二值化图   
    ret, image_bi = cv2.threshold(image_gray, 0, 255, cv2.THRESH_OTSU)
    # print(color)
    if(color!=0):
        image_bi = 255 - image_bi

    # 膨胀操作，采用矩形卷积核
    kernel_dilate = cv2.getStructuringElement(cv2.MORPH_RECT, (3,3))
    image_dilate = cv2.dilate(image_bi, kernel_dilate)

    # 获取轮廓
    contours, hierarchy = cv2.findContours(image_dilate, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    # 根据轮廓找到对应的最小的矩形框，并且进行排序
    chars = list()
    for contour in contours:
        rect = cv2.boundingRect(contour)
        chars.append(rect)
    chars.sort()

    images_chars = list()
    for rect in chars:
        # rect的[0],[1],[2],[3]分别是矩阵左上点的横坐标，纵坐标，矩阵的宽和矩阵的高
        x,y,w,h = rect[0],rect[1],rect[2],rect[3]
        # 根据w和h判断是否为一个字符，若是，则将对应的矩阵加入列表
        if (w*1.5<h<w*3.5)and(w>10):
            images_chars.append(image_dilate[y:y+h,x:x+w])

    # 显示一下
    for i,j in enumerate(images_chars):  
        plt.subplot(1,8,i+1)
        plt.imshow(images_chars[i],cmap='gray')
    plt.show()

    #下一步需要用到的是各个字符的图像即images_chars
    return images_chars

    
