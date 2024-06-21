import spacy
import pytesseract
import cv2

pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"
nlp = spacy.load("es_core_news_sm")

image = cv2.imread("Page_1.jpg")
ret, thresh1 = cv2.threshold(image, 120, 255, cv2.THRESH_BINARY)
text = str(pytesseract.image_to_string(thresh1, config="--psm 6"))

doc = nlp(text)

for ent in doc.ents:
    print(
        f"""
            {ent.text = }
            {ent.start_char = }
            {ent.end_char = }
            {ent.label_ = }
            spacy.explain('{ent.label_}') = {spacy.explain(ent.label_)}"""
    )
