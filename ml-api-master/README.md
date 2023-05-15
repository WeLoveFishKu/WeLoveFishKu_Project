Base URL:

 <p >
  <a href="https://model.fishku.id">model.fishku.id</a>
</p>

### Ikan Kembung
- Endpoint
  - /predict?ikan=kembung
- Method
  - POST
- Request Body
  - file = files
- Response

```json
{
    "prediction": "Tidak Segar"
}
```

### Ikan Tongkol
- Endpoint
  - /predict?ikan=tongkol
- Method
  - POST
- Request Body
  - file = files
- Response

```json
{
    "prediction": "Segar"
}
```

### Ikan Bandeng
- Endpoint
  - /predict?ikan=bandeng
- Method
  - POST
- Request Body
  - file = files
- Response

```json
{
    "prediction": "Tidak Segar"
}
```

----

Base URL:

<p >
  <a href="https://apis.fishku.id">apis.fishku.id</a>
</p>

### Get data ikan
- Endpoint
  - /consumer/detect/fish/
- Method
  - GET
- Response

```json
{
    "banyak": 3,
    "data": [{
            "id": 1,
            "name": "kembung",
            "photo": null
        },
        {
            "id": 2,
            "name": "tongkol",
            "photo": null
        },
        {
            "id": 3,
            "name": "bandeng",
            "photo": null
        }
    ]
}
```
