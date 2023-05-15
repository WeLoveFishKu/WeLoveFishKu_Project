import flask
import io
import string
import time
import os
import numpy as np
import tensorflow as tf
from PIL import Image
from flask import Flask, jsonify, request
from typing import Tuple, Dict, Union, Optional
from werkzeug.utils import secure_filename



def prepare_image(img: Image, desired_size: Tuple=(224, 224)) -> np.ndarray:
    """
    Preparing the images before transforming into nd.array & predictions.

    The reason why i set (224, 224) to images is because i used 
    mobilenet that required 224 x 224.

    params:
        - img: Image that we want to predict.
        - desired_size: Size of images that we wants to predict.
    return:
        - nd.array format. 
    """
    img = Image.open(io.BytesIO(img))
    img = img.resize(desired_size)
    img = np.array(img)
    img = np.expand_dims(img, axis=0)
    return img


models: Dict = {
    'bandeng': 'bandeng_mata.h5',
    'tongkol': 'tongkol_kepala.h5',
    'kembung': 'kembung_kepala.h5'
}


def predict_result(img: Image, model_h5: Union[Dict, str]= 'bandeng_mata.h5') -> str:
    """
    params:
        - img: Image that we want to predict and shows the result.
        - desired_size: Name of model that have been trained.
    return:
        - str format, wether segar or tidak segar.
    """

    model = tf.keras.models.load_model(model_h5)
    return "Segar" if model.predict(img)[0][0] > 0.5 else "Tidak Segar"


app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def infer_image():
    file = request.files.get('file')
    
    if 'file' is None or file.filename == "":
        return {'error': 'Please try again. The Image does not exist'}

    img_bytes = file.read()
    img = prepare_image(img_bytes)

    ikan = request.args.get('ikan')
    if ikan == 'bandeng':
        prediction = predict_result(img)
    elif ikan == 'tongkol':
        prediction = predict_result(img, 'tongkol_kepala.h5')
    elif ikan == 'kembung':
        prediction = predict_result(img, 'kembung_kepala.h5')
    elif ikan == 'bandeng_insang':
        prediction = predict_result(img, 'bandeng_insang.h5')
    elif ikan == 'tongkol_insang':
        prediction = predict_result(img, 'tongkol_insang.h5')
    elif ikan == 'kembung_insang':
        prediction = predict_result(img, 'kembung_insang.h5')


    basepath = os.path.dirname(__file__)
    file_path = os.path.join(
        basepath, 'uploads', secure_filename(
            prediction + "_" + file.filename
            )
        )
    file.save(file_path)

    return {
        'prediction': prediction,
        'state': 'false' or 'true',
    }
    

@app.route('/', methods=['GET'])
def index():
    return 'Machine Learning Inference'


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')






# mypy apis.py --ignore-missing-import