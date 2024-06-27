import numpy as np
import cv2

def get_binary_image(img_gray, threshold):
        width, height = img_gray.shape
        img_bi = np.ones(img_gray.shape, np.uint8)
        for x in range(width):
            for y in range(height):
                pixel = img_gray[x,y]
                if pixel >= threshold: 
                    img_bi[x,y]=0
                else:
                    img_bi[x,y]=255
        return img_bi
    
def floodfill(image, x, y, new_value):
    height, width = image.shape[::-1]
    old_value = image[x, y]

    if not old_value == new_value:
        pixel_queue = [(x, y)]
        while pixel_queue:
            # get a pixel
            x, y = pixel_queue.pop()
            if 0 <= x < width and 0 <= y < height:
                if image[x, y] == old_value:
                    # fill
                    image[x, y] = new_value
                    # add neighbors to the queue
                    pixel_queue.extend([(x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)])
    
    return image

def bitwise_not(image):
    return 255-image

def solution2(img_input,img_result):
    # read the image as a grayscale image
    img_gray = cv2.imread(img_input, cv2.IMREAD_GRAYSCALE)

    # binary processing of images
    img_bi = get_binary_image(img_gray, 127)

    # floodfill the image
    img_floodfill = img_bi.copy()
    img_floodfill = floodfill(img_floodfill, 1, 1, 255)

    # process the floodfill image
    img_floodfill_inv = bitwise_not(img_floodfill)
    img_holefill = img_bi | img_floodfill_inv
    # img_rebuild = bitwise_not(img_holefill)

    cv2.imwrite(img_result, img_holefill)

if __name__ == '__main__':
    img_input = "C:\\Users\\86188\\Desktop\\210010101_DIP_Project1\\image_pro_1_2.jpg"
    img_result = "C:\\Users\\86188\\Desktop\\210010101_DIP_Project1\\result images\\result_1_2.png"
    solution2(img_input,img_result)


