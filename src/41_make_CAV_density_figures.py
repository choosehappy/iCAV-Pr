# **Source Code Authors :**   
# > *Andrew Janowczyk*  

# **Published in Research Article:**  
# > [Computational Analysis of Routine Biopsies Improves Diagnosis and Prediction of Cardiac Allograft Vasculopathy](https://www.ahajournals.org/doi/10.1161/CIRCULATIONAHA.121.058459)  
# > *by Eliot G. Peyster, Andrew Janowczyk, Abigail Swamidoss, Samhith Kethireddy, Michael D. Feldman and Kenneth B. Margulies*  
# *Originally published 11 Apr 2022*  
#  
# > [Supplementary Material](https://www.ahajournals.org/action/downloadSupplement?doi=10.1161%2FCIRCULATIONAHA.121.058459&file=10.1161.circulationaha.121.058459_supplemental_materials.pdf)  
#
# **Publisher**
# [Journal of American Heart Association (JAHA) - Circulation](https://www.ahajournals.org/journal/circ)


# ---
# jupyter:
#   jupytext:
#     formats: ipynb,py:light
#     text_representation:
#       extension: .py
#       format_name: light
#       format_version: '1.5'
#       jupytext_version: 1.11.0
#   kernelspec:
#     display_name: Python 3
#     language: python
#     name: python3
# ---

import numpy as np
import matplotlib.pyplot as plt

#a=np.loadtxt("nodrop_50_cd31_dab.csv",delimiter=",",usecols=2,skiprows=1)
a=np.loadtxt("21cd31_dab.csv",delimiter=",",usecols=2,skiprows=1)

plt.rcParams['figure.figsize'] = [20, 7]

import matplotlib.font_manager as font_manager
font = font_manager.FontProperties(style='normal', size=20)
plt.rcParams.update({'font.size': 20})


_=plt.hist(a,bins=50,facecolor="none",edgecolor="b",density=True)
# freq,bin=np.histogram(a,bins=500,density=True)
# plt.plot(bin[:-1],freq)
plt.xlim([0, a.max()])
plt.xlabel("Object size in pixels", fontsize=20)
plt.ylabel("Frequency",fontsize=20)
plt.title("DAB Object Size Distribution", fontsize=20)

_=plt.hist(a,bins=50,facecolor="none",edgecolor="b",density=True)
# freq,bin=np.histogram(a,bins=500,density=True)
# plt.plot(bin[:-1],freq)
leg=[]
for v,c,label in zip(np.quantile(a,[0,.25,.50,.75]),["g","y","r","black"],["0% Quantile","25% Quantile","50% Quantile","75% Quantile"]):
    leg.append(plt.axvline(x=v,color=c, label=label))
plt.legend(handles=leg,prop=font)  
plt.xlim([0, a.max()])
plt.xlabel("Object size in pixels", fontsize=20)
plt.ylabel("Frequency",fontsize=20)
plt.title("DAB Object Size Distribution", fontsize=20)

# +
#_=plt.hist(a,bins=50,facecolor="none",edgecolor="b",density=True)
freq,bin=np.histogram(a,bins=500,density=True)
plt.plot(bin[:-1],freq)

leg = []
leg.append(plt.axvline(x=a.mean(),color='red',label = "Mean",  linewidth=5))
plt.axvline(x=a.mean()-a.std(),color='green')

plt.axvline(x=a.mean()-.5*a.std(),color='purple',label=".5 Std")
leg.append(plt.axvline(x=a.mean()+.5*a.std(),color='purple',label=".5 Std (S1)"))

leg.append(plt.axvline(x=a.mean()+a.std(),color='green',label="1 Std (S2)"))
leg.append(plt.axvline(x=a.mean()+1.5*a.std(),color='pink',label="1.5 Std (S3)"))

plt.axvline(x=a.mean()-2*a.std(),color='yellow')
leg.append(plt.axvline(x=a.mean()+2*a.std(),color='yellow',label="2 Std (S4)"))
leg.append(plt.axvline(x=a.mean()+3*a.std(),color='black',label="3 Std (S5)"))
plt.legend(handles=leg,prop=font)  
plt.xlim([0, a.max()])
plt.title("DAB Object Size Distribution", fontsize=20)
plt.ylabel("Frequency",fontsize=20)
plt.xlabel("Object size in pixels", fontsize=20)


# +
# # 1 pixel = 0.0633
# conversion=1/.0633
# biobins_inmp2=[["Microvessels",[10,78.4],"red"],
# ["Small precapillary arterioles",[78.5,314],"green"],
# ["Medium veins and arterioles",[315,1000],"yellow"],
# ["Medium-large",[1000,2500],"black"],
# ["Large",[2501,13000],"blue"]]


biobins=[["Microvessels",[158],"red"],
["Small precapillary arterioles",[1239],"green"],
["Medium veins and arterioles",[4964],"yellow"],
["Medium-large",[15809],"black"],
["Large",[39523],"blue"]]



# +
#_=plt.hist(a,bins=50,facecolor="none",edgecolor="b",density=True)
#_=plt.hist(a,bins=50,facecolor="none",edgecolor="b",density=True)
freq,bin=np.histogram(a,bins=500,density=True)
plt.plot(bin[:-1],freq)

leg = []

for label,bounds,color in biobins:
    leg.append(plt.axvline(x=bounds[0],color=color, label=label))
plt.xlim([0, a.max()])
plt.title("DAB Object Size Distribution", fontsize=20)
plt.ylabel("Frequency",fontsize=20)
plt.xlabel("Object size in pixels", fontsize=20)
plt.legend(handles=leg,prop=font)  

# -


