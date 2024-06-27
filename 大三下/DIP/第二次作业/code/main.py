from find import detect
from splitCharacter import split_char
from match import match


if __name__ == '__main__':
    # 调用detect函数，分割出车牌图像，注意路径
    image,color = detect('C:/Users/86188/Desktop/car_final/img/origin.jpg')
    # 调用split_char函数，分割出每个字符的图像
    splited_chars = split_char(image,color)
    # 字符匹配
    match(splited_chars)