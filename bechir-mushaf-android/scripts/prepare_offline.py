#!/usr/bin/env python3
"""Prepare a fully bundled offline reading interface and verify all 604 pages."""
from pathlib import Path
from concurrent.futures import ThreadPoolExecutor, as_completed
from urllib.request import urlopen
import time

root=Path(__file__).resolve().parents[1]
assets=root/"bechir-mushaf-android/app/src/main/assets"
pages=assets/"pages"
pages.mkdir(parents=True,exist_ok=True)
source="https://raw.githubusercontent.com/ahmedabdalharith/Quran-Mushaf-Data/main/data/images/hafs-tajweed/"
def fetch_page(n):
    target=pages/f"{n:03}.webp"
    if target.exists() and target.stat().st_size>1000:
        return
    for attempt in range(4):
        try:
            with urlopen(source+f"{n:03}.webp",timeout=35) as response:
                content=response.read()
            if len(content)<1000 or content[:4]!=b"RIFF" or content[8:12]!=b"WEBP":
                raise ValueError(f"Invalid WEBP for page {n}: {len(content)} bytes")
            target.write_bytes(content)
            return
        except Exception:
            if attempt==3: raise
            time.sleep(2*(attempt+1))
with ThreadPoolExecutor(max_workers=10) as pool:
    futures={pool.submit(fetch_page,n):n for n in range(1,605)}
    for f in as_completed(futures):f.result()
missing=[n for n in range(1,605) if not (pages/f"{n:03}.webp").exists()]
if missing:raise RuntimeError(f"Missing pages: {missing}")
html=(root/"bechir-mushaf-test/index.html").read_text(encoding="utf-8")
assert source in html
html=html.replace(source,"pages/")
html=html.replace("<title>مصحف بشير — تجربة الفاتحة</title>","<title>مصحف بشير</title>")
html=html.replace("</style></head>","#downloadPages{display:none!important}</style></head>",1)
(assets/"index.html").write_text(html,encoding="utf-8")
print("Bundled 604 validated WEBP pages and local index.html; total MB:",round(sum(p.stat().st_size for p in pages.glob("*.webp"))/1048576,1))
