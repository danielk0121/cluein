# Project Memory

## Git Commit Method

**Use `printf` + `-F` flag instead of HEREDOC/subshell.**

The `$()` subshell with HEREDOC (`cat <<'EOF'`) can prompt for user input in some environments. Always use this pattern instead:

```bash
printf 'commit message\n\nCo-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>\n' > /tmp/commit_msg.txt && git commit -F /tmp/commit_msg.txt
```

- No `$()` subshell
- No HEREDOC
- Supports multiline commit messages
- Preserves Co-Authored-By tag

**주의**: 명령어를 반드시 **한 줄**로 작성한다. 줄바꿈이 끼어들면 작은따옴표가 깨져서 `<`, `>` 가 리다이렉션으로 해석되어 입력 프롬프트가 뜬다.
