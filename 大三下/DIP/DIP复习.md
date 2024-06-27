# DIP 第1章
## 一、What is digital Image Processing?
### 1.Image and common image formats
图像的定义（P10）和常见的图像格式（P15）
### 2.Definition of digital image
数字图像的两种定义（P10，P11）
### 3.Pixel and pixel value
像素的含义（P10），像素值的含义（P13）
### 4.Digital image processing and its major tasks
数字图像处理的含义和其关注的2个主要任务（P16）
### 5.DIP与其他一些概念的区别（P18）
### 6.注意：数字化是对真实场景的近似（P13）

## 二、Human Visual System 人类视觉系统
### 1.Structure of the human eye
人眼结构（P20，P21）
### 2.Blind-spot experiment
盲点（P22）
### 3.Light and the Electromagnetic Spectrum 光与电磁频谱
***(1)Light***
光的定义（P23），可见光（P25）
***(2)Photon***
光子相关（P26）
***(3)Color we perceive***
感知的光由谁决定（P27）
***(4)Gray level***
灰度（P28）
***(5)Three basic quantities to describe the quality of a chromatic light source***
三个描述彩色光源质量的基本量及其含义（P29）
***(6)Radiation and microwaves***
各种射线、微波及应用（P31-39）

## 三、Image Aquisition 图像采集
### 1.How are images generated?
图像如何生成（P40）
### 2.Sensors
传感器（P41-44）

## 四、Image Sampling and Quantization 图像采样和量化
### 1. Two steps of creating a digital image
常见数字图像的两个步骤和相关含义（P45）

## 五、Representation 表示 计算（P49）

## 六、Spatial & Intensity Level Resolution 空间和强度级别分辨率
### 1.Spatial resolution
空间分辨率的定义，取决于什么（P51）
### 2.Intensity level resolution
强度级别分辨率的定义，相关描述（P52）
### 3.How many ... are good?（P56-58）

## 七、Zooming and Shrinking Digital Images 缩放和缩小数字图像
### 1.Zooming
含义（P59-60），方法（P60-61）
### 2.Shrinking
含义（P63），做法（P63-65）

## 八、连通性相关概念
### 1.Connectivity
连通性的重要性，定义（P66）
### 2.Adjacency
相邻的分类（P67-68）
### 3.Path
通路的定义，闭合通路的定义（P69）
### 4.Connected Set and Region 连通集和区域
P71一系列概念：
connected连通，connected component连通分量，connected set连通集，region区域，boundary边缘

### 九、Distance Measures 各种距离计算（P72-76）

### 十、Key stages in DIP 数字图像处理的关键阶段
各种任务的流程图（P77-85）


# DIP 第2章 
# Image Enhancement(Point Processing) 图像增强之点处理
## 一、Image Enhancement 图像增强的相关概念
### 1.What is Image Enhancement?
图像增强的含义和这么做的理由（P2）
### 2.Gray Level 灰度范围相关（P6）
### 3.Two broad categories of image enhancement techniques 图像增强的分类（P7）
spatial domain image enhancement 空域图像处理的基本形式（P8）
point processing 点处理的基本形式（P9）
point processing and mask processing or filtering 区别点处理和mask...（P10）

## 二、Point Processing 点处理
### 1.Thresholding 阈值分割
作用和公式（P12）
### 2.Logarithmic Transformations 对数变换
作用和公式（P14）
### 3.Power Law Transformations 幂律变换
公式和描述（P16）
### 4.Gamma Correction 伽马校正
公式（P19），先验和应用（P21）
### 5.Gray Level Slicing 灰度层级划分
含义和描述（P22），公式（P23）
### 6.Bit Plane Slicing 比特平面划分
高低比特位决定了什么（P25）
### 7.Image Subtraction 图像相减
公式和值的处理（P27），应用（P30-32）
### 8.Image Averaging 图像平均
公式（P33-34）
### 9.Image Histograms 图像直方图
公式（P39），作用（P40），特性（Properties，P42，45）
***直方图均衡化***
- basic idea（P46）
- 假设/条件及说明（P47-48）
- 公式（P49）
- transformation function 变换函数的两种形式（P52-53），推导（P51）
- steps 各个步骤（P54开始）
step2:Si = ∑(j=0到i) Pr(rj)
step3:Si = 原Si*（灰度级别数-1），四舍五入得到新的灰度级别
step4:把属于同一个新的灰度级的概率相加得到新的概率




# DIP 第3章 
# Image Enhancement(Spatial Filtering) 图像增强之空域滤波
## 一、Basic of spatial filtering 空域滤波基础
### 1.neighborhood 邻域相关说法（P3）
### 2.use of spatial masks for image processing(spatial filters) 有哪些空域滤波器（P4）

## 二、Smoothing spatial filters 平滑空域滤波器
### 1.Smoothing linear filters 平滑线性滤波器
***(1)spatial correlation 空间相关性***
公式（P6）
***(2)spatial convolution 空间卷积***
公式，与相关性的关系（P7）
***(3)dealing with missing edge pixels 处理边缘处的像素***
方法（P11）
***(4)spatial smoothing filtering 空间平滑滤波***
- averaging filter 平均滤波器
做法，作用，滤波器图示（P13），说法（P17）
- weighted averging 权重平均（可能也是平均的一种）
说法，滤波器图示（P16）
- 作用 （P12）
### 2.Order-statistics filters 统计排序滤波器
分类，含义（P21）
***(1)median filter 中值滤波***
描述，用处（P24）
***(2)max filter 最大值滤波***
公式，作用（P25），缺点（P27）
***(3)min filter 最小值滤波***
公式，作用（P25），缺点（P27）
***(4)adaptive filter 自适应滤波***
描述（P29），好处，key insight中心思想（P30），3个目的（P32）

## 三、Sharpening spatial filters 锐化空域滤波器
### 1.1st derivative filters 一阶导数滤波器
***(1)1st derivative of a function 一个函数的一阶微分***
公式（P36）
***(2)1st derivative of f(x,y) f(x,y)的一阶微分***
公式（P56），大小计算公式（P57）
***(3)各种应用***
simplest（P58），Roberts（P59），we will use（P61），Sobel（P62）
### 2.2nd derivative filters 二阶导数滤波器
***(1)2st derivative of a function 一个函数的二阶微分***
公式（P39），图（P40-41）
***(2)2st derivative is more useful for image enhancement than the 1st derivative***
二阶导相比一阶导更适合于图像增强的几个原因（P42）
***(3)The Laplacian 拉普拉斯算子***
- 公式（总，对x偏导，对y偏导，P43）
- 拉普拉斯算子
拉普拉斯算子公式和滤波器图示（P44）
一步到位公式（P48）
变种（P50）
两种表达方式（P52）
High Boost Filtering和滤波器图示（P53-54）
- 应用“凸显”（P46）

## 四、Combining Spatial Enhancement Methods 组合空间增强方法
例子（P65-68）






# DIP 第4章 
# Image Enhancement(Filtering in the Frequency Domain) 图像增强之频域滤波
## 一、Big idea 基本思想
### 1.表示（P3）
周期性函数可以表示成一系列sin和cos函数的和，非周期性函数可以表示成一系列sin和cos函数的积分
### 2.The most important characteristics of Fourier Transform 傅里叶变换的重要性（P4）

## 二、Fourier Transform 傅里叶变换
### 1.Fourier Transform of f(x) f(x)的傅里叶变换
***积分形式***
公式（P5）
***离散形式DFT***
公式（P6），计算（P7），术语（P8-9）
### 2.frequency domain and frequency component 频域和频率分量的含义（P10）
### 3.glass prism and Fourier Transform 棱镜与傅里叶变换（P11）
### 4.傅里叶变换的二维表示
***积分形式***
公式（P12）
***离散形式DFT***
公式（P13），术语（P14），性质（P15-25）
性质（Properties）:
- translation property 平移性质（P15）
- Rotation property 旋转性质（P18）
- Average value 平均值（P19）
- Convolution 卷积（P20）
- impulse function 脉冲函数（P21-22），Note(P23)
- correspond to 各种对应关系（P25）

## 三、Steps of Filtering in the Frequency Domain 频域滤波步骤
### 1.Steps 步骤（总述）（P26）
### 2.filter 滤波器（P27）
### 3.做法和Note（P28）
### 4.流程图（P29）

## 四、Some Basic Frequency Domain Filters 一些基本频域滤波器
### high/low frequencies are responsible for... 高低频影响什么（P30）
### three types of frequency Domain Filters 三类基本频域滤波器（P30）
### 1.Notch filter 限波滤波器
公式（P31）
### 2.Lowpass filter:smoothing filters 低通滤波器：平滑滤波器
低通高斯滤波器函数公式（P33），傅里叶变换对（P34）
### 3.Highpass filter:sharpening filters 高通滤波器：锐化滤波器
高通高斯滤波器函数公式（P35）

## 五、Smoothing Frequency Domain Filters 平滑频域滤波器
### 基本模型和低通的含义（P36）
### 1.Ideal lowpass filter 理想低通滤波器
描述（P38），公式（P39-40），图像ringing and blurring分析（P44）
### 2.Butterworth lowpass filter 巴特沃斯低通滤波器
公式（P45），ringing and blurring（P49）
### 3.Gaussian lowpass filter 高斯低通滤波器
公式（P50）

## 六、Sharpening Frequency Domain Filters 锐化频域滤波器
### 与低通的公式的区别和高通的含义（P55）
### 1.Ideal highpass filter 理想高通滤波器
公式（P58）
### 2.Butterworth highpass filter 巴特沃斯高通滤波器
公式（P60）
### 3.Gaussian highpass filter 高斯高通滤波器
公式（P63）


# DIP 第5章 
# Image Restoration 图像重建
## 一、一些基础知识
### 1.What is Image Restoration?
attempt to 目的（P3），与enhancement对比（P4）
### 2.priori knowledge about the degragation process 退化过程的先验知识 3个（P5）
### 3.A Model of Image Degradation and Restoration 一种图像退化与恢复模型
***(1)空域***
公式（P6），流程图（P7）
***(2)Frequency domain 频域***
公式（P6），流程图（P8）
***(3)Resotration model 恢复模型相关***
目的，描述（P9）

## 二、Noise 噪声
### 1.The sources of noise 噪声源（P10）
### 2.Noise Models 噪声模型
***(1)Gaussian noise 高斯噪声***
公式，图（P12），原因（P17）
***(2)Rayleigh noise 瑞利噪声***
公式，图（P12），作用（P17）
***(3)Erlang(Gamma) noise 伽马噪声***
公式，图（P13），作用（P17）
***(4)Exponential noise 指数分布噪声***
公式，图（P13），作用（P17）
***(5)Uniform noise 均匀分布噪声***
公式，图（P14），作用（P17）
***(6)impulse noise 冲击噪声***
公式，图（P14），又名，noise level的含义（P15），command（归一化，p默认值，其他类型noise，P16），在哪些情况下出现（P17）
***example 例子（P18-20）***
### 3.Periodic Noise 周期噪声
***(1)概述***
arise from 来源（P21），characteristics 特性（P21），processing method 处理方法（P21）
***(2)Periodic Noise Reduction by Frequency Domain Filter 频域滤波器的周期性降噪***
- Bandreject filter 带阻滤波器：ideal，butterworth，Gaussian 的公式（P32-33），图（P34-35）
- Bandpass filter 带通滤波器：公式（P36），例子和作用（P37）
- Notch filter 限波滤波器：概述（P38），summetry 对称性（P39），reject的公式（P40-41），pass的公式（P42），图（P43-44）
### 4.Estimation of Noise Parameters 噪声参数的估计
***(1)基本描述（P23）***
***(2)计算的量***
mean 均值（P25），variance 方差（P25），Gaussian的公式（P26），Rayleigh的公式（P27）
### 5.Restoration of Noise Only Degradation 仅噪声退化的恢复
***(1)model 模型（P28）***
***(2)相关说法（P29）***

## 三、Estimating the Degradation Function 估计退化函数
Blind deconvolution 盲反卷积 的原因（P45）
### 1.Estimation by Image Observation 图像观测估计
概述（P46），公式（P47）
### 2.Estimation by Experimentation 实验估算
assume 假设（P48），impulse（P49）
### 3.Estimation by Modeling 通过建模进行估计
公式（P51），motion blur 运动模糊的例子（P53-56）

## 四、Inverse Filtering and Wiener Filtering
### 1.Inverse Filtering 反向滤波
公式（P57），tell us ...（P57），情况和如何做（P58）
### 2.Wiener Filtering 维纳滤波
又名，合并了什么信息（P60），公式，假设(assumption)和推导（P61，62，63-65），例子（P66-68）


# DIP 第6章 
# Morphological Image Processing-1 形态学图像处理-1
## 一、基本概念
### 1.What Is Morphology?
describe 描述了什么，basic idea 基本想法，mathematical foundation 数学基础（P3）
0表示background而1表示foreground（P4）
### 2.Basic Concepts of Set Theory 集合论的基本概念（P5-11）
set，∈，∉，Ø，typical set specification，subset，union，intersection，disjoint，complement，difference
translation，reflection
structuring element（P9，11），Fit，Hit，Miss（P9）
## 二、Fundamental Operations 基本操作
### 1.Dilation 膨胀 （看是否hit：有相交...）
公式（P13，24），例子（P14-21，22-23，25-30），note:origin point（P26），watchout（P30）
### 2.Erosion 腐蚀 （看是否fit：全部相交...）
公式（P31，36），例子（P32-33，34-35，37-42），note:origin point（P37），watchout（P42）
### 3.Application 两者的应用（P43）
### 三、Compound Operations 复合运算
### 1.Opening 开操作（先腐蚀再膨胀）
公式（P45），例子（P45-50）
### 2.Closing 闭操作（先膨胀再腐蚀）
公式（P51），例子（P51-56）
### 3.Properties of Opening and Closing 两者的属性
公式（？，P57），对图像的作用（P58），形态学处理的例子（P59-60）
### 4.Hit-or-Miss Transform 命中不命中
公式（P61-63），例子（P64-68）

# DIP 第7章 
# Morphological Image Processing-2 形态学图像处理-2
## 一、形态学处理的各种应用
### 1.Boundary extraction 边界提取
公式（P3），例子（P4-5）
### 2.Region filling 区域填充
attempt to（P6），算法（P7），例子（P8-28），注意（P27笔记），Note（P28）
### 3.Extraction of connected components 提取连通分量
算法（P29），例子（P30-42）
### 4.Convex Hull 凸包
- (B：黑色前景，白色背景，×无要求，符合要求时将白色变为黑色...)
概念和算法（P43），例子（P44-45），缺点与改进（P46）
### 5.Thinning and thickening 细化和扩张
***Thinning:***公式（P47，49），例子（P48-55）..(符合要求后去掉中间那个点...)
***Thickening:***说法，result in（P56）
### 6.Skeletons 骨架
公式（P58），例子（P59-60）

## 二、形态学扩展到Grayscale image
说法...（？P62-63）
### 1.Erosion
公式（P64），例子（P65-68）
### 2.Dilation
公式（P69），例子（P70-76）
### 3.nonflat...erosion and dilation...
公式（P77-78）补集与镜像...

# DIP 第8章 
# Image Segmentation-1 图像分割-1
## 一、Segmentation 分割的一些基本概念
purpose 目的（P3），stop when 何时停止分割（P3），based on 基于什么（P3），alogorithms based on 算法基于什么（P8），the first step of 是什么的第一步（P4），applications 应用（P4），controllable 要求...可控（P7），3种检测（P9）

## 二、Edge Detection 边缘检测
### 1.基本概念
***(1)概述和边缘类型***（P10）
***(2)边缘和区域边界的含义***（P12）
### 2.边缘与导数
***(1)1st dericaitve 一阶导数***
特点（P14），用处（P15），Gradient operator公式（P20），Gradient direction方向（P21），常见算子（P22），例子（P23-25）
***(2)2st dericaitve 二阶导数***
- Laplacian
特点（P14），用处（P15），描述（P16），少用的原因（P26）
- Laplacian of Gaussian (LoG)
公式（P28-29），例子（P30-31），Remark（两者的作用...，P32）

***(3)dericaitve and noise 导数与噪声***
sensitive（P17），smoothing重要（P18），Edge Point如何决定（P19）
### 3.Canny
***(1)optimal edge dector properties 最佳边缘检测器特性*** 3个（P35）
***(2)流程图***（P36）
***(3)公式和具体做法***（P37-40）

## 三、Edge Link and Boundary Detection  边缘连接与边界检测
### 1.Local Processing
做法和概念(strength,direction,Criteria)（P44-45），例子（P46）
### 2.Global Processing
描述（P47），Solution（P48）
***Hough Transform 霍夫变换***（P49-69）
- 变换公式和变换pair的含义（P49-50），极坐标形式（P54）
- 算法（P51）
- 边缘连接问题（P59）
- 一些说法...（P53，69）
- detecting circles 检测圆（P64-65）
- 例子（P52，55-58，60-63，66-68）

# DIP 第9章 
# Image Segmentation-2 图像分割-2
## 一、Thresholding 阈值
### 1.基本知识
***(1)说法...***（P3）
***(2)公式***单个，多个，更普遍的形式（P4-6）
***(3)分类***（P6）
***(4)key factors 关键因素***（P7）
***(5)例子***（P8-9）
### 2.Basic Global Thresholding 基本全局阈值
***(1)to do...,successful in...***（P10）
***(2)算法***（P13）
***(3)remarks(about initial)***（P14）
***(4)例子***（P11-12，15-16）
### 3.Basic Adaptive Thresholding 基本自适应阈值
***(1)说法***（P17）
***(2)例子***（P18-20）
***(3)Otsu算法***
思想（P21，23），公式（P22，25），特点（P23），思考、思路、问题（P29），例子（P24，26-28）
### 4.Problem with Single Value Thresholding （P30）

## 二、Region based segmentation 基于区域的分割
### 1.based on...（P32）
### 2.Region Growing 区域生长
生长过程和remark（P37），例子（P38），前面英语部分...（？P34-36）
### 3.Region Splitting and Merging 区域拆分和合并
做法（P39），算法（P40），例子（P41-42）

## 三、Morphological Watersheds 形态学分水岭
概念（P43-35），算法（P46），例子（P47-52），应用（P53），水坝构造（P54-56），过度分割问题和解决方法（P57-60）


# DIP 第10章 
# Color Image Processing-1 彩色图像处理-1
## 一、Color Fundamental 彩色基础
### 1.概述
motivation 动机，full color，Pseudo color（P3）
### 2.人的与彩色感知相关的器官
human brain 人脑（P4），
人眼（P7-9） human retinas have 2 types of photoreceptors
### 3.Light and Spectra 光与光谱（P5-6）
### 4.Way for specifying colors 指定颜色的方法
***(1)RGB***
Note（P10），add（p11），difference（p12）
***(2)HSB***
概念（P13-14），公式（P15）
***(3)CIE***（P16-17）

## 二、Color Models 彩色模型
### 1.概述
又名，purpose，types，typical（P18），assumption（P19）
### 2.各种模型
***(1)RGB&RGBA***
概念，pixel depth（P20），例子（P21-22），CIE XYZ system表达（P23）
***(2)CMY&CMYK***
CMY概念和与RGB的转换（P24），CMYK概念（P25），conversions 互相转换（P27-28）
***(3)HSI***
RGB和CMY缺点引出HSI（P29），be obtained from and HSI space概念（P31），注意（P33，39-40），conversions（与RGB）（P37-38）
***(4)YIQ&YUV***（P41）

## 三、Pseudo Color Processing 伪彩色处理
### 1.Problem in Color Image（P42-44）
### 2.概述：principle，分类（P45）
### 3.Intensity Slicing 强度分层
做法（P46），公式（P47），例子（P48-52）
### 4.Gray Level to Color Transformation 灰度到颜色的转换
Note（P53），例子（P54-56）


# DIP 第11章 
# Color Image Processing-2 彩色图像处理-2
## 一、Full Color Processing 全色处理
2 categories to process vectorial images 处理矢量图像的2个类别（P3），图（P4-5），希望等效...（P6）
### 1.Color Transformations 彩色变换
***(1)Color transformation 彩色变换***
公式（P7，10），remark（P14），例子（P8-9，11-13）
***(2)Color Complements 颜色互补***
useful for（P15），Note（P17），例子（P16-17）
***(3)Color Slicing 颜色切片***
概述（P18），公式（P19），例子（P20-21）
***(4)Tone and Color Corrections 色调和颜色校正***
概述（P22），princile of...（P23），Note（P24），例子（P25-27）
***(5)Saturation Enhancement 饱和度增强***（P28-29）
***(6)Hue Enhancement 色调增强***（P30-31）
***(7)Histogram Processing 直方图处理***
概述（P32），例子（P33-35）
### 2.Smoothing and Sharpening 平滑和锐化
***(1)概述***（P36）
***(2)Smoothing 平滑***
公式（P38），例子（P37，39-45）
***(3)Sharpen 锐化***
公式（P46），例子（P47）
### 3.Color Segmentation 颜色分割
***(1)概述和分类***（P48-49）
***(2)... in HSI***
概述（P50，52），公式（P53-54），问题和办法（P55-56），例子（P51，57）
### 4.Noise in Color Image 彩色图像噪声
概述（P58），Note（P60），例子（P59，61）



# DIP 第12章 
# Representation & Description-1 表示和描述-1
## 一、Overview 
after...（P3），used to...（P3），Representing region in 2 ways（P3）,tasks（P4），sensitivity要求（P5）

## 二、Representation 表示
### 1.Chain Codes 链码
***(1)原始链码***
例子（P7），问题（P8）
***(2)resample 重新采样***（P9-10）
***(3)normalized chain codes 规范化链码***
- 循环数字串看最小（平移不变性）（P11）
- Bi = (Ai-Ai-1) mod N 减法（旋转不变性）（P12）
***(4)chain code smoothing 链码平滑***
做法：根据template“虚”改“实”（P14），例子（P15），SCC chain code斜率链码（P16）
### 2.Polygonal Approximations 多边形近似
概述：goal和3 methods（P17）
***(1)Minimum Perimeter Polygons 最小周长多边形***
“内外玻璃，橡皮筋拉紧...”（P18-20）
***(2)Merging Techniques 合并技术***
做法：某点到两点线段的距离小于阈值则可合并...（P21），例子（P22）
***(3)Splitting Techniques 分裂技术***
is to...（目的）（P23），3步做法（P23），例子（P24）
### 3.Landmark points 标志点（P25-26）
### 4.Signatures 签名（P27-30）说法（P28）
### 5.Boundary Segments 边界段（P31-33）
### 6.Skeletons 骨架
3点需求（P37），假设（P38），算法（P39-40），说法与步骤（P41-42），例子（P34-36，43-44）
### 7.Boundary Fitting 边界拟合
公式（P45-47），例子（P48）

## 三、Boundary Descriptors 边界描述符
### 1.Position 位置（P50）
### 2.Direction 方向（P51）
### 3.Major axis 主轴（P52）
### 4.Length of boundary 边界长度（P53）
### 5.Enclose Area 封闭区域
分类（P54），例子和公式？（P55-56）
### 6.Curvature 曲率（P57-58）


# DIP 第13章  
# Representation & Description-2 表示和描述-2
## 一、Fourier Descriptors 傅里叶描述符

公式（P3-4），说法和结论（P5-6），例子（P7）
## 二、Regional Descriptors 基于区域的描述
### 1.simple descriptors
分类（P9），说法、例子（P10-11）
### 2.Topological Descriptors 拓扑描述符
概念和说法（P12），公式（Euler number E...）（P13-14）
### 3.Texture 纹理
引入/说法（P15），例（P16-17），使用（P18），Statistical（P19-20）
### 4.Grey Level Co-occurence Matrix 灰度共生矩阵
相对texture的改进..（P22），各种相关概念的定义（P23-27），例（28-29）
### 5.Moments of Two Dimensional Functions 二维函数的矩
说法（P30），一堆公式（P31-36），例（P37-38）

## 三、Relational Descriptors 基于关系的描述
概述和分类（P39）
### 1.Directed line segments（P40-41）
### 2.Tree（P42-43）

## 四、Color Descriptors 彩色描述符
主要是color histogram彩色直方图
概述（P44），例（P45-47，51），公式（P48-50），usage（P52）


# DIP 第14章 
# Object Recognition 物体识别
## 一、概述（各种概念）
Object Recognition,objects,classification,clustering（P2）
features,Representation,classifier（P3）
feature space（P5）

## 二、Classification 分类
### 1.Recognition based on Decision-Theoretic Method
（基于决策理论方法的识别）公式（P8），例（P9）
### 2.pattern classification 模式分类
说法（P10），例（P11），应用（P19-21）
### 3.Minimum Distance Classifier 最小距离分类器
公式（P13-14），例？（P15）
### 4.Feature Selection 特征选择
问题（P16），含义&例（P17），
### 5.Performance of Classifier 分类器的表现（P18）

## 三、Clustering 聚类
### 1.Cluster Analysis 聚类分析
含义，特点，说法（P22）
### 2.Distance Measure 距离测量（P24）
### 3.K-means
过程/做法（P26），例（P27-31），讨论（P32）

## 四、Application:Biometric 生物识别
### 1.概述
challenge,problem,3 methods（P33）
### 2.Traditional Methods 传统方法
token-based（P34），Knowledge-based（P35）
### 3.Biometrics:what you are/do!
特点（P37），架构图（P38），应用（P39），挑战（P40-45）
working modes（P46），multimodal biometric system图（P67）
***Palmprint 掌纹识别（例）***
架构图（P47），why（P48），各种feature（P50）
line feature extraction method（P54-55）
texture based feature extraction（P57-62）
new solution（P63-65）


# DIP 第15-16章 
# Image Compression 图像压缩
## 一、Fundamentals 基本原理
1.Motivation （P3）
2.compression and decompression 加解压（P4）
3.Applications（P5）
4.lossless and lossy（P6）
5.data and information（P7-8）

## 二、Data Redundancy 数据冗余性
概念（P10），公式（P10-11），basic...（基本的冗余性）（P12）
### 1.Coding Redundancy 编码冗余性
公式（P13-14），例（P15-17），说法（P18）
### 2.Interpixel Redundancy 像素间冗余性
remark（P20），properties（P21），reduced approches（P22），run-length coding（P23）
### 3.Psychovisual Redundancy 心理视觉冗余
引入/理由（P26），basic cognitive procedure 基本认知顺序？（P27），说法（P28），例（P29-32）

## 三、Fidelity Criteria 保真度准则
2大类（P33）
### 1.Objective Fidelity Criterion 客观保真度标准
***(1)root-mean-square(rms) error***（P34）
***(2)mean-square signal-to-noise ratio(snr)***（P35）
### 2.Subjective Fidelity Criterion 主观保真度标准
说法（P36），例（P37-38）

## 四、Compression Model 压缩模型
流程（P39）
### 1.Source encoder & decoder
考虑3种冗余性（P40），steps of source encoder（P41），remark（P42）
### 2.Channel encoder & decoder（P43）

## 五、Lossless Image Compression 无损图像压缩
### 1.概述
又名，motivation，application（P44），2...operations，approaches（P45）
### 2.Variable-Length Coding 变长编码（去除编码冗余）
分类和remark（P46）
***(1)Huffman coding 哈夫曼编码***
步骤（P47），例（P48-56）
***(2)Arithmetic coding 算术编码***
概述（P3），例/做法..（P4-12），公式？（P6）
### 3.LZW coding （去除像素间冗余）
概述（P13-14），例/做法（P15-23），remark（P24）
### 4.Hybrid coding 混合编码（P25-26）

## 六、Lossy Image Compression 有损图像压缩
### 1.概述
说法（P30），流程图（P31），要求should be...（P32）
### 2.Typical transforms
***(1)DFT***概述（P33），公式（P34）
***(2)DCT***概述（P35），公式（P36）
***(3)WHT***概述（P37），公式（P38）
***(4)KLT***概述（P41）
***(5)Wavelet Transform***
概述（P44，46），流程图（P45），例（P47-49）
### 3.Transform selection（P39-40，42-43）

## 七、JPEG（P50-52）
