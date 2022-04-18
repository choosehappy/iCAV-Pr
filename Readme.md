

# iCAV-Pr Project
This project has the code necessary to extract biomarker data from CD31 and Movat stained whole slide images. This is the supplemental method mentioned in this research article. 
https://www.ahajournals.org/doi/10.1161/CIRCULATIONAHA.121.058459

## _CD31 Workflow_

QuPath-0.2.3, an open source software platform for digital pathology and WSI image analysis, was used for data extraction from CD31 slides. HistoQC software was downloaded from github using this repository, [https://github.com/choosehappy/HistoQC](https://github.com/choosehappy/HistoQC).

### Segmentation: Annotations and Nuclei Detection

To initiate feature discrimination, the WSIs stained with CD31 were imported into a new QuPath project. The following steps were completed using Apache Groovy scripts.
 - Preprocessing
 - Annotation
 - Nuclei detection


### Preprocessing

Preprocessing included setting the image type (Brightfield H-DAB), background, and stain deconconvolution. These were the parameters used:

setColorDeconvolutionStains('{"Name" : "H-DAB CD31",  
"Stain 1" : "Hematoxylin", "Values 1" : "0.59666 0.75249 0.27885 ",  
"Stain 2" : "DAB", "Values 2" : "0.24042 0.48171 0.84271 ",  "Background" : " 235 235 243 "}');

### Annotation
The following pixel classifiers and their corresponding parameters were generated in order to annotate specific regions or features of interest within the annotated ROI. Resolution parameters may differ based on the slides.

 - A Region of Interest (ROI) was annotated using a pixel classifier and classified as “CD31ROI”. The following threshold values were used: Resolution: Low (4.02μm/px), Channel: Average, Prefilter: Gaussian, Smoothing Sigma: 4, Threshold: 231, Minimum object size: 4000μm^2, and Minimum hole size: 4000μm^2. 
	
 - CD31Tissue_classifier_v3: Resolution: Very High (0.50μm/px), Channel: Average, Prefilter: Gaussian, Smoothing Sigma: 4, Threshold: 235, Minimum object size: 10μm^2, and Minimum Hole Size: 10μm^2.
	
 - CD31DAB_classifier_v3: Resolution: Full (0.25μm/px), Channel: DAB, Prefilter: Gaussian, Smoothing Sigma: 1.5, Threshold: 0.2, Minimum object size: 10μm^2, and Minimum Hole Size: 10μm^2
	
 - CD31HEMA_classifier_v3: Resolution: Full(0.25μm/px), Channel: Hematoxylin, Prefilter: Gaussian, Smoothing Sigma: 1.5, Threshold: 0.2, Minimum object size: 10μm^2, and Minimum Hole Size: 10μm^2

### Nuclei Detection

Having classified the annotations, positive cell detection was run with the following parameters on the “CD31 Tissue” annotations.  Supplemental Figure S.1 defines the parameters used for running positive cell detection.
![Fig_s1](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Fig_s1.png)

The annotation and nuclei data was exported as GeoJSON files for each WSI. Each WSI file therefore has a corresponding nuclei.json file and annotation.json file.

### Extracting Features

The corresponding nuclei and annotation GeoJSON files were then opened using Python’s Geojson and Shapely libraries. Areas for tissue, DAB, hematoxylin, and nuclei were calculated using the Shapely library and collected in four respective Pandas dataframes. The four data frames were then saved to tis.csv, dab.csv, hem.csv, and nuc.csv. Each of these files contain the cohort, filename, and area. In dab.csv, the corresponding Shapely polygons and polygon coordinates were also saved. 

### Binning and DAB dilation
Dab.csv was loaded in a pandas dataframe and rows where DAB areas were in the bottom 0.01% were dropped to compensate for errors.

### Defining Boundaries for Various Binning Processes
 1. Quartile bin boundaries were calculated using statistics from the csv dataframe for the 25th, 50th, and 75th percentiles.
 2. Standard deviation bin boundaries, outlined in Supplemental Figure S.2, were calculated by the addition of a new column to the data frame that holds the z-score for that row. The z-score was calculated using the equation (Area - Mean)/(Standard Deviation). The boundaries were then defined as a) within ±0.5 standard deviations from the mean b) within ±1.0 standard deviation excluding the area between ±0.5 standard deviations c) within ±1.5 standard deviation excluding the area between  ±1.0 standard deviations d) within ±2.0 standard deviation excluding the area between  ±1.5 standard deviations e) within ±3.0 standard deviation excluding the area between  ±2.0 standard deviations f) outside of ±3.0 standard deviations.
 ![Fig_s2](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Fig_s2.png)
  3. Biological bin boundaries were defined as follows (conversion used: area of 1 pixel^2 = 0.0633μm^2):

***Table S.1:  Biologically inspired boundary definitions.*** 
![Tab_s1](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s1.png)



### DAB Dilation and Binning Process
The DAB dataframe was grouped by cohorts and filename. For each group, its corresponding nuc.json file was opened and its nuclei detections were read using shapely and geojson.  Shapely’s unary_union was used for dilating the DAB annotation, STRtree was used for searching nuclei centroids, and Shapely’s Within function was used to identify nuclei within DAB. Nuclei that were within overlapping dilated regions were not double counted.  The following process was performed by dilating the DAB annotation based on the bins. Supplemental Table  S.2 outlines the bin dilation sizes.

- findNucsInDAB_qrt: identify nuclei that were within quartile boundaries
- findNucsInDAB_std: identify nuclei that were within standard deviation boundaries
- findNucsInDAB_bio: identify nuclei that were within biological boundaries
- findNucsInDAB_excl: identify nuclei that were within the excluded DAB boundaries

***Table S.2: Binning strategies and their corresponding bin dilation sizes.***
![Tab_s2](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s2.png)

The final CD31 results were collected and saved to CSV.

## Movats Workflow
Movat's Pentachrome classification started with extracting 2000 x 2000 tiles from the WSI. 
### Segmentation
Movat’s Pentachrome stained tiles included regions of myocytes, collagen, basement membrane, stroma, and nuclei. Supplemental Table S.3 presents each of these features, their corresponding color, and the threshold range used.
 ![Tab_s3](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s3.png)
 
Tiled images were opened in the BGR color space using OpenCV and a Gaussian blur with a 5 x 5 kernel was applied to this image. A low pass filter, gaussian kernel, was applied in order to reduce random noise in the tiled images. This Gaussian blurred image was then converted to HSV. Feature detection in the HSV space was chosen because BGR includes luminance which makes feature discrimination difficult. HSV abstracts hue while separating illumination making it better suited for feature detection. Each stained feature correlated with a specific range of colors in HSV space defined in Supplemental Table S.3.

However, certain slides were overstained and this created issues with the segmentation process which would lead to false positives for stromal detections. To address this, overstained images were manually identified and brightened using Python’s OpenCV convertScaleAbs with an alpha (contrast) value of 1.5 and a beta (brightness) value of 0 prior to segmentation.  

Binary masks of each color’s minimum and maximum range were created. Before calculating the area of the binary masks, post processing using Skimage’s morphology functions was needed to reduce noise. The following Supplemental Table S.4 describes the various morphological parameters used. A myocardium mask was also generated by filling holes in the myocyte mask and dilating it until large sheets of myocytes were confluent.
![Tab_s4](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s4.png)

### Feature Quantification
After morphology functions were applied, Python’s NumPY was used to calculate the total area for each binary mask, where each binary mask was represented by a specific feature. The features were summed by WSI file and grouped by cohort. This Movats data was then saved to a CSV file.

### Merging CD31 and Movats Data
CD31 and Movats data was merged using Pandas based on corresponding filenames. From these, Supplemental Table S.5 lists the features extracted. 
![Tab_s5](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s5.png)

### Supplemental Results:
***Assessing Established Predictive Clinical Risk Factors for CAV:***
The starting point for ClinCAV-Pr Model development and optimization was not a traditional ‘empty’ (containing no variables) or ‘full’ (containing all variables) model.  Instead, we chose to start with predictive risk factors recently established in a large, multicenter observational study on CAV trajectories1.  These risk factors – donor age, donor sex, donor cigarette use, recipient LDL cholesterol at 1-year post transplant, presence of +DSA at 1-year, and history of cellular rejection at 1-year – were used to develop and test as model as described in the statistical methods section (and in an identical fashion to other study models).  As shown in Supplemental Figure S.3, model performance was overall fairly poor, with an AUROC of 0.63 and an accuracy of 62%.  This poor performance justified the additional feature selection steps employed to develop the final ClinCAV-Pr in this experiment, which utilizes several of these previously established risk factors, but which also adds additional risk factors related to histology/rejection history and donor and recipient medical history. 
![Fig_s3](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Fig_s3.png)

***HistoCAV-Pr Model Performance by ISHLT Grade:***
Recognizing that there was a baseline imbalance between No-CAV and PrE-CAV groups in the proportion of EMBs with 0R vs. 1R rejection grades (no-CAV: 57.8% 1R grade, while PrE-CAV 76% 1R grade), we explored whether model predictions were strongly associated with underlying ISHLT grade.  Supplemental Table S.6 shows that misclassifications by the HistoCAV-Pr Model were overall fairly balanced between grades, with a 13.3% (8/60) misclassification rate for 0R EMBs, and a 16.7% misclassification rate for 1R EMBs (p=0.59).  Looking more closely, no ISHLT grade does not significantly impact misclassification rates within or between the No-CAV and PrE-CAV groups.  This suggests that model performance is not significantly affected by underlying ISHLT grade, and that misclassified cases are unlikely to be the result of the underlying ISHLT grade.
![Tab_s6](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s6.png)


PrE-CAV = pre-early cardiac allograft vasculopathy (CAV) patients, defined as patients experiencing overt CAV by 5-years post-transplant but without overt diagnosis at 1-year post-transplant.  No-CAV = patients without overt CAV at 6-years post-transplant. ISHLT = international society for heart and lung transplantation

***Morphologic Model Performance by Slide Scanner Manufacturer:***
While slide scanners are commonplace in pathology departments across the world, there are several different companies manufacturing these devices.  Different manufacturers utilize different methods to create digital images, with differences in hardware (charge-coupled device chips vs. lighting bulbs) and software approaches (stitching vs. compression) potentially affecting final image appearance2.  Prior literature has shown that these differences can potentially impact downstream image analysis pipeline performance.  Therefore, we deliberately incorporated digitized slides from two different slide scanner devices in our study to permit an assessment of the resilience of the image analysis pipeline and subsequent models to variations in slide scanner technology.  Digital images arising from each scanner were combined into a single cohort prior to randomization to training/test sets.  As shown in Supplemental Table S.7, the overall classification performance (combining training and test set results) of both the HistoCAV-Dx and HistoCAV-Pr Models were unaffected by slide scanner manufacturer.  Note that because training and test sets are combined for this analysis, the overall AUROC and accuracy are designed to reflect only overall differences in performance between slide scanners, and not overall validated model performance (as presented in the primary Results). 
![Tab_s7](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s7.png)

![Tab_s8](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s8.png)

![Tab_s9](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s9.png)

![Tab_s10](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s10.png)

### References:
1.	Loupy, A., et al., Identification and Characterization of Trajectories of Cardiac Allograft Vasculopathy After Heart Transplantation. Circulation, 2020. 141(24): p. 1954-1967.
2.	Janowczyk, A., A. Basavanhally, and A. Madabhushi, Stain Normalization using Sparse AutoEncoders (StaNoSA): Application to digital pathology. Computerized Medical Imaging and Graphics, 2017. 57: p. 50-61.
