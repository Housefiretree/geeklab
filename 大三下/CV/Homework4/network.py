import torch
import torch.nn as nn
import torch.nn.functional as F

class Net(nn.Module):
    def __init__(self):
        super(Net,self).__init__()
        ### YOUR CODE HERE
        pass
        self.layer1 = nn.Sequential(
            nn.Conv2d(in_channels = 3,out_channels = 6,kernel_size = (5,5),stride = 1,padding = 0),
            nn.MaxPool2d(kernel_size = 2, stride=2)
        )
        self.layer2 = nn.Sequential(
            nn.Conv2d(in_channels = 6,out_channels = 16,kernel_size = (5,5),stride = 1,padding = 0),
            nn.MaxPool2d(kernel_size = 2, stride=2)
        )
        self.layer3 = nn.Sequential(
            nn.Linear(400,120),
            nn.Linear(120,84),
            nn.Linear(84,10),
        )
        ### END YOUR CODE

    def forward(self, x):
        ### YOUR CODE HERE
        pass
        out = self.layer1(x)
        out = self.layer2(out)
        out = out.view(out.size(0), -1)
        x = self.layer3(out)
        ### END YOUR CODE
        return x