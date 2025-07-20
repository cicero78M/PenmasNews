-- PostgreSQL schema for Penmas News

CREATE TABLE IF NOT EXISTS editorial_event (
  event_id SERIAL PRIMARY KEY,
  event_date TIMESTAMP NOT NULL,
  topic TEXT NOT NULL,
  judul_berita TEXT,
  assignee VARCHAR(50),
  status VARCHAR(20) DEFAULT 'draft',
  content TEXT,
  summary TEXT,
  image_path TEXT,
  tag TEXT,
  kategori TEXT,
  created_by TEXT REFERENCES penmas_user(user_id),
  updated_by TEXT REFERENCES penmas_user(user_id),
  created_at TIMESTAMP DEFAULT NOW(),
  last_update TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS press_release_detail (
  event_id INTEGER PRIMARY KEY REFERENCES editorial_event(event_id),
  judul TEXT,
  dasar TEXT,
  tersangka TEXT,
  tkp TEXT,
  kronologi TEXT,
  modus TEXT,
  barang_bukti TEXT,
  pasal TEXT,
  ancaman TEXT,
  catatan TEXT
);
