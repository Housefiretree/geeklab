import numpy as np
import cv2

def median_filter(img):
    H,W = img.shape[::-1]
    img_filtered = np.zeros(img.shape)

    for i in range(0,W):
        for j in range(0,H):
            if(i==0 or i==W-1 or j==0 or j==H-1):
                img_filtered[i,j] = img[i,j]
            else:
                img_filtered[i,j] = np.median(img[i-1:i+2,j-1:j+2])
    
    return img_filtered

def Sobel(img,img_filtered):
    H,W = img.shape[::-1]
    img_sobel = np.zeros(img.shape)
    
    # define Sobel operator
    G_x = np.array([[-1,0,1],[-2,0,2],[-1,0,1]])
    G_y = np.array([[-1,-2,-1],[0,0,0],[1,2,1]])

    # calculate gradient values and derivative through Sobel operators 
    for i in range(0,W-2):
        for j in range(0,H-2):
            v = sum(sum(G_x*img_filtered[i:i+3,j:j+3]))
            h = sum(sum(G_y*img_filtered[i:i+3,j:j+3]))
            img_sobel[i+1,j+1] = np.sqrt((v**2)+(h**2))
    
    return img_sobel
    
def get_result(img,img_sobel,img_filtered,threshold):
    H,W = img.shape[::-1]
    arr_result = np.zeros(img.shape, dtype="uint8")

    # process image
    for i in range(0,W):
        for j in range(0,H):
            if(i==0 or i==W-1 or j==0 or j==H-1):
                arr_result[i,j] = img[i,j]
            elif img_sobel[i,j] >= threshold:
                # edge remain unchanged
                arr_result[i,j] = img[i,j]
            else:
                # else,median filtering
                arr_result[i,j] = img_filtered[i,j]

    return arr_result


def solution1(img_input,img_result,threshold):
    # read image and resize it
    img_origin = cv2.imread(img_input, cv2.IMREAD_GRAYSCALE)
    img = cv2.resize(img_origin, (440, 280))
    
    # median filtering
    img_filtered = median_filter(img)

    # get sobel image
    img_sobel = Sobel(img,img_filtered)
    
    # get result
    arr_result = get_result(img,img_sobel,img_filtered,threshold)

    # write the result
    cv2.imwrite(img_result, arr_result)
    # print(img.shape)
    # print(arr_result.shape)

if __name__ == '__main__':
    img_input = "C:\\Users\\86188\\Desktop\\210010101_DIP_Project1\\hit.png"
    img_result = "C:\\Users\\86188\\Desktop\\210010101_DIP_Project1\\result images\\result_1_1.png"
    threshold = 150
    solution1(img_input, img_result,threshold)

