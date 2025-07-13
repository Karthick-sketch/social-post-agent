# agent/db.py
"""
Tiny SQLite helper for storing social-post records.

Schema
------
posts(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    brief           TEXT    NOT NULL,   -- 1-sentence input from marketer
    content         TEXT    NOT NULL,   -- JSON blob with captions & hashtags
    user_content    TEXT    NOT NULL,   -- human-edited content
    images          TEXT    NOT NULL,   -- JSON array of image URLs
    status          TEXT    NOT NULL,   -- DRAFT | APPROVED | SCHEDULED
    created_at      TEXT    NOT NULL,   -- ISO-8601 UTC timestamp
    updated_at      TEXT    NOT NULL    -- ISO-8601 UTC timestamp
)
"""

from __future__ import annotations

import sqlite3
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Dict, List, Optional

ISO = "%Y-%m-%dT%H:%M:%SZ"  # always store UTC “Zulu” time


class DB:
    def __init__(self, path: str | Path = "posts.sqlite") -> None:
        self.conn = sqlite3.connect(path, check_same_thread=False)
        self.conn.row_factory = sqlite3.Row
        self._ensure_schema()

    # ------------------------------------------------------------------
    # Public API
    # ------------------------------------------------------------------
    def insert_post(self, brief: str, content: str, status: str) -> int | None:
        """Insert a new record and return its auto-increment id."""
        ts = self._now()
        cur = self.conn.execute(
            "INSERT INTO posts (brief, content, status, created_at, updated_at) "
            "VALUES (?, ?, ?, ?, ?)",
            (brief, content, status, ts, ts),
        )
        self.conn.commit()
        return cur.lastrowid

    def get(self, post_id: int) -> Dict[str, Any]:
        row = self.conn.execute(
            "SELECT * FROM posts WHERE id = ?", (post_id,)
        ).fetchone()
        if row is None:
            raise ValueError(f"Post id {post_id} not found")
        return dict(row)

    def update_user_content(self, post_id: int, user_content: str) -> None:
        ts = self._now()
        self.conn.execute(
            "UPDATE posts SET user_content = ?, updated_at = ? WHERE id = ?",
            (user_content, ts, post_id),
        )
        self.conn.commit()

    def update_status(self, post_id: int, status: str) -> None:
        ts = self._now()
        self.conn.execute(
            "UPDATE posts SET status = ?, updated_at = ? WHERE id = ?",
            (status, ts, post_id),
        )
        self.conn.commit()

    def list_posts(self, status: Optional[str] = None) -> List[Dict[str, Any]]:
        sql = "SELECT * FROM posts"
        params: tuple[Any, ...] = ()
        if status:
            sql += " WHERE status = ?"
            params = (status,)
        sql += " ORDER BY created_at DESC"
        cur = self.conn.execute(sql, params)
        return [dict(row) for row in cur.fetchall()]

    def delete(self, post_id: int) -> None:
        self.conn.execute("DELETE FROM posts WHERE id = ?", (post_id,))
        self.conn.commit()

    def close(self) -> None:
        self.conn.close()

    # ------------------------------------------------------------------
    # Internal helpers
    # ------------------------------------------------------------------
    def _ensure_schema(self) -> None:
        self.conn.execute(
            """
            CREATE TABLE IF NOT EXISTS posts
            (
                id           INTEGER PRIMARY KEY AUTOINCREMENT,
                brief        TEXT NOT NULL,
                content      TEXT NOT NULL,
                user_content TEXT,
                images       TEXT,
                status       TEXT NOT NULL,
                created_at   TEXT NOT NULL,
                updated_at   TEXT NOT NULL
            )
            """
        )
        self.conn.commit()

    @staticmethod
    def _now() -> str:
        """Return current UTC time as ISO-8601 string (seconds precision)."""
        return datetime.now(tz=timezone.utc).strftime(ISO)
