import re
from datetime import datetime
from zoneinfo import ZoneInfo

NB_TZ = ZoneInfo("America/Moncton")

_HOURS_RE = re.compile(
    r"(\d{1,2})(?::(\d{2}))?\s*(am|pm)"
    r"\s*[\-\u2013]\s*"
    r"(\d{1,2})(?::(\d{2}))?\s*(am|pm)",
    re.IGNORECASE,
)

ALWAYS_OPEN_STRINGS = {"24/7", "24 h", "24h", ""}

def _to_minutes(hour, minute, meridiem):
    hour = hour % 12
    if meridiem.lower() == "pm":
        hour += 12
    return hour * 60 + minute

def parse_opening_hours(hours_str):
    if not hours_str or hours_str.strip() in ALWAYS_OPEN_STRINGS:
        return None, None, True
    match = _HOURS_RE.search(hours_str)
    if not match:
        return None, None, True
    oh, om, o_mer, ch, cm, c_mer = match.groups()
    open_minutes = _to_minutes(int(oh), int(om or 0), o_mer)
    close_minutes = _to_minutes(int(ch), int(cm or 0), c_mer)
    return open_minutes, close_minutes, False

def is_currently_open(hours_str, at=None):
    open_min, close_min, always_open = parse_opening_hours(hours_str)
    if always_open:
        return True
    at = at or datetime.now(NB_TZ)
    now_minutes = at.hour * 60 + at.minute
    if open_min <= close_min:
        return open_min <= now_minutes < close_min
    return now_minutes >= open_min or now_minutes < close_min