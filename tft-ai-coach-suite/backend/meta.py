from typing import Dict, List

class MetaStore:
    def __init__(self):
        # default tiers; replace via /meta/push
        self.data: Dict[str, int] = {
            "celestial_blessing": 3,
            "second_wind": 2,
            "thrill_of_the_hunt": 2
        }

    def update(self, new: Dict[str, int]):
        self.data.update({k:int(v) for k,v in new.items()})

    def recommend(self, offered: List[str]):
        scored = [{"augment": a, "tier": self.data.get(a, 0)} for a in offered]
        best = max(scored, key=lambda x: x["tier"]) if scored else None
        return {"scored": scored, "best": best["augment"] if best else None}
