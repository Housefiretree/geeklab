import numpy as np
import torch


def conv_nested(image, kernel):
    """A naive implementation of convolution filter.

    This is a naive implementation of convolution using 4 nested for-loops.
    This function computes convolution of an image with a kernel and outputs
    the result that has the same shape as the input image.

    Args:
        image: numpy array of shape (Hi, Wi).
        kernel: numpy array of shape (Hk, Wk). Dimensions will be odd.

    Returns:
        out: numpy array of shape (Hi, Wi).
    """
    Hi, Wi = image.shape
    Hk, Wk = kernel.shape
    out = np.zeros((Hi, Wi))

    ### YOUR CODE HERE
    for n in range(Hi):
        for m in range(Wi):
            for k in range(Hk):
                for l in range(Wk):
                    if(n-k+1>=0 and n-k+1<Hi and m-l+1>=0 and m-l+1<Wi):
                        out[n,m] += image[n-k+1,m-l+1]*kernel[k,l]
    ### END YOUR CODE

    return out


def conv_nested_torch(image, kernel):
    """You should use the torch library to implement this function. To do that, you need to install and import torch.

    Args:
        image: numpy array of shape (Hi, Wi).
        kernel: numpy array of shape (Hk, Wk). Dimensions will be odd.

    Returns:
        out: numpy array of shape (Hi, Wi).
    """
    kernel_torch = torch.tensor(
        kernel, dtype=torch.float32
    ).unsqueeze(0).unsqueeze(0)  # Expand the kernel dimensions as (N, C, H, W) for torch compatibility (N=1, C=1)

    image_torch = torch.tensor(
        image, dtype=torch.float32
    ).unsqueeze(0).unsqueeze(0)

    out = np.zeros(image.shape)

    ### YOUR CODE HERE
    # Create a layer of convolution
    Hi,Wi = image.shape
    Hk,Wk = kernel.shape
    layer = torch.nn.Conv2d(in_channels=1, out_channels=1, kernel_size = (Hk,Wk),stride=1,padding=1)
    # Load kernel to convolution layer
    layer.weight.data = kernel_torch
    # Execute convolution operation
    tmp = layer(image_torch)
    # Convert the output tensor to numpy array
    out = np.copy(tmp.detach().numpy())
    out.resize(Hi,Wi)
    ### END YOUR CODE

    return out


def zero_pad(image, pad_height, pad_width):
    """ Zero-pad an image.

    Ex: a 1x1 image [[1]] with pad_height = 1, pad_width = 2 becomes:

        [[0, 0, 0, 0, 0],
         [0, 0, 1, 0, 0],
         [0, 0, 0, 0, 0]]         of shape (3, 5)

    Args:
        image: numpy array of shape (H, W).
        pad_width: width of the zero padding (left and right padding).
        pad_height: height of the zero padding (bottom and top padding).

    Returns:
        out: numpy array of shape (H+2*pad_height, W+2*pad_width).
    """

    H, W = image.shape
    out = None

    ### YOUR CODE HERE
    out = np.pad(image,((pad_height, pad_height),(pad_width,pad_width)),'constant',constant_values=0)
    ### END YOUR CODE
    return out


def conv_fast(image, kernel):
    """ An efficient implementation of convolution filter.

    This function uses element-wise multiplication and np.sum()
    to efficiently compute weighted sum of neighborhood at each
    pixel.

    Hints:
        - Use the zero_pad function you implemented above
        - There should be two nested for-loops
        - You may find np.flip() and np.sum() useful

    Args:
        image: numpy array of shape (Hi, Wi).
        kernel: numpy array of shape (Hk, Wk). Dimensions will be odd.

    Returns:
        out: numpy array of shape (Hi, Wi).
    """
    Hi, Wi = image.shape
    Hk, Wk = kernel.shape
    out = np.zeros((Hi, Wi))

    ### YOUR CODE HERE
    # term = zero_pad(image, int((Hk-1)/2), int((Wk-1)/2))
    # for i in range(int((Hk-1)/2), Hi+int((Hk-1)/2)):
    #     for j in range(int((Wk-1)/2), Wi+int((Wk-1)/2)):
    #         out[i-int((Hk-1)/2), j-int((Wk-1)/2)] = np.sum(term[i-int((Hk-1)/2):i+int((Hk-1)/2)+1, j-int((Wk-1)/2):j+int((Wk-1)/2)+1] * np.flip(kernel))
    # WHY THIS IS WRONG??????🤯it is promised that Hk and Wk are odd!😨
    term = zero_pad(image, int((Hk-1)/2), int((Wk-1)/2))
    kernel_flip = np.flip(np.flip(kernel, 0), 1)
    for i in range(int((Hk-1)/2), Hi+int((Hk-1)/2)):
         for j in range(int((Wk-1)/2), Wi+int((Wk-1)/2)):
             out[i-int((Hk-1)/2), j-int((Wk-1)/2)] = np.sum(term[i-int((Hk-1)/2):i+int((Hk-1)/2)+1, j-int((Wk-1)/2):j+int((Wk-1)/2)+1] * kernel_flip)
    ### END YOUR CODE

    return out


def cross_correlation(f, g):
    """ Cross-correlation of image f and template g.

    Hint: use the conv_fast function defined above.

    Args:
        f: numpy array of shape (Hf, Wf).
        g: numpy array of shape (Hg, Wg).

    Returns:
        out: numpy array of shape (Hf, Wf).
    """

    out = None
    ### YOUR CODE HERE

    ### END YOUR CODE

    return out


def zero_mean_cross_correlation(f, g):
    """ Zero-mean cross-correlation of image f and template g.

    Subtract the mean of g from g so that its mean becomes zero.

    Hint: you should look up useful numpy functions online for calculating the mean.

    Args:
        f: numpy array of shape (Hf, Wf).
        g: numpy array of shape (Hg, Wg).

    Returns:
        out: numpy array of shape (Hf, Wf).
    """

    out = None
    ### YOUR CODE HERE

    ### END YOUR CODE

    return out


def normalized_cross_correlation(f, g):
    """ Normalized cross-correlation of image f and template g.

    Normalize the subimage of f and the template g at each step
    before computing the weighted sum of the two.

    Hint: you should look up useful numpy functions online for calculating 
          the mean and standard deviation.

    Args:
        f: numpy array of shape (Hf, Wf).
        g: numpy array of shape (Hg, Wg).

    Returns:
        out: numpy array of shape (Hf, Wf).
    """

    out = None
    ### YOUR CODE HERE
    # caculate normalized-cross-correlation

    ### END YOUR CODE

    return out
