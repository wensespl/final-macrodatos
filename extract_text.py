import pytesseract
import cv2
# import matplotlib.pyplot as plt

pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'

# load the original image
image = cv2.imread('Page_1.jpg')  

# plt.figure(figsize=(10,10))
# plt.imshow(image)

# convert the image to black and white for better OCR
ret,thresh1 = cv2.threshold(image,120,255,cv2.THRESH_BINARY)

# pytesseract image to string to get results
text = str(pytesseract.image_to_string(thresh1, config='--psm 6'))
print(text)