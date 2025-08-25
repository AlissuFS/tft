from fastapi import APIRouter
from meta import MetaStore

router = APIRouter()
store = MetaStore()

@router.post("/meta/push")
def push_meta(data: dict):
    store.update(data)
    return {"ok": True}

@router.get("/meta")
def get_meta():
    return store.data

@router.post("/recommend")
def recommend(payload: dict):
    augments = payload.get("augments", [])
    return store.recommend(augments)
