from fastapi import FastAPI
from routes import router

app = FastAPI(title="TFT Coach Backend")
app.include_router(router)

@app.get("/")
def root():
    return {"status": "ok"}
