listener_file="${ORACLE_HOME}/network/admin/listener.ora"

if [ -f "$listener_file" ]; then
    sed -E -i 's/HOST[[:space:]]*=[[:space:]]*[^)]*/HOST=0.0.0.0/g' "$listener_file"
    lsnrctl stop LISTENER >/dev/null 2>&1 || true
    lsnrctl start LISTENER >/dev/null
fi
