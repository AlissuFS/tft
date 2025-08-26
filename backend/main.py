from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(title="TFT Coach Backend")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def root():
    return {"message": "TFT Coach Backend Online"}

@app.get("/predict")
def predict():
    return {"best_comp": "Assassins", "best_augment": "S-tier augment"}

@app.get("/history")
def history():
    return {"matches": []}

@app.get("/export")
def export():
    return {"status": "export ready"}
