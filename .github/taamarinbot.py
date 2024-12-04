import asyncio
import os
import sys
from telethon import TelegramClient
from telethon.tl.functions.messages import GetMessagesRequest

API_ID = os.environ.get("API_ID")
API_HASH = os.environ.get("API_HASH")

CHAT_ID = int(os.environ.get("CHAT_ID"))
MESSAGE_THREAD_ID = int(os.environ.get("MESSAGE_THREAD_ID"))
BOT_TOKEN = os.environ.get("BOT_TOKEN")
VERSION = os.environ.get("VERSION")
COMMIT = os.environ.get("COMMIT")
# TAGS = os.environ.get("TAGS")
MSG_TEMPLATE = """
{version}

Commit:
{commit}

[source](https://github.com/taamarin/box.manager)
[module](https://github.com/taamarin/box_for_magisk)

#apk #manager #BFR #root
""".strip()

def get_caption():
    msg = MSG_TEMPLATE.format(
        version=VERSION,
        # tags=TAGS,
        commit=COMMIT
    )
    if len(msg) > 1024:
        return COMMIT
    return msg

def check_environ():
    if BOT_TOKEN is None:
        print("[-] Invalid BOT_TOKEN")
        exit(1)
    if CHAT_ID is None:
        print("[-] Invalid CHAT_ID")
        exit(1)
    if MESSAGE_THREAD_ID is None:
        print("[-] Invalid MESSAGE_THREAD_ID")
        exit(1)
    if VERSION is None:
        print("[-] Invalid VERSION")
        exit(1)
    if COMMIT is None:
        print("[-] Invalid COMMIT")
        exit(1)
    # if TAGS is None:
        # print("[-] Invalid TAGS")
        # exit(1)

async def main():
    print("[+] Uploading to telegram")
    check_environ()
    files = sys.argv[1:]
    print("[+] Files:", files)
    if len(files) <= 0:
        print("[-] No files to upload")
        exit(1)
    print("[+] Logging in Telegram with bot")
    script_dir = os.path.dirname(os.path.abspath(sys.argv[0]))
    session_dir = os.path.join(script_dir, "bot.session")
    async with await TelegramClient(session=session_dir, api_id=API_ID, api_hash=API_HASH).start(bot_token=BOT_TOKEN) as bot:
        caption = [""] * len(files)
        caption[-1] = get_caption()
        print("[+] Caption: ")
        print("---")
        print(caption)
        print("---")
        print("[+] Sending")
        sent_messages = await bot.send_file(entity=CHAT_ID, file=files, caption=caption, reply_to=MESSAGE_THREAD_ID, parse_mode="markdown")

        # Pin the last sent message
        if sent_messages:
            last_message_id = sent_messages[-1].id
            await bot.pin_message(entity=CHAT_ID, message=last_message_id)

        print("[+] Done!")

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except Exception as e:
        print(f"[-] An error occurred: {e}")
