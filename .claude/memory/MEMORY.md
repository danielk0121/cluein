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
