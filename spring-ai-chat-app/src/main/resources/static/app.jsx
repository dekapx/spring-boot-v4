const { useEffect, useRef, useState } = React;

function ChatApp() {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState("");
    const endRef = useRef(null);

    useEffect(() => {
        endRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages, busy]);

    async function sendMessage(event) {
        event.preventDefault();
        const message = input.trim();
        if (!message || busy) return;

        const previousMessages = messages;
        setMessages(current => [...current, { role: "user", content: message }]);
        setInput("");
        setError("");
        setBusy(true);

        try {
            const response = await fetch("/api/chat", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    message,
                    history: previousMessages.map(({ role, content }) => ({ role, content }))
                })
            });

            if (!response.ok) {
                const problem = await response.json().catch(() => ({}));
                throw new Error(problem.detail || `Request failed (${response.status})`);
            }

            const data = await response.json();
            setMessages(current => [...current, { role: "assistant", content: data.reply }]);
        } catch (err) {
            setError(err.message || "Unable to contact the chat service.");
        } finally {
            setBusy(false);
        }
    }

    function clearChat() {
        if (busy) return;
        setMessages([]);
        setError("");
    }

    return (
        <main className="app-shell">
            <header className="topbar">
                <div>
                    <div className="eyebrow">LOCAL AI ASSISTANT</div>
                    <h1>Spring AI Chat</h1>
                    <p>Powered by qwen3-coder:30b · Ollama</p>
                </div>
                <button className="clear-button" onClick={clearChat} disabled={busy}>
                    Clear chat
                </button>
            </header>

            <section className="chat-panel" aria-label="Chat conversation">
                <div className="messages">
                    {messages.length === 0 && (
                        <div className="welcome">
                            <div className="welcome-icon">✦</div>
                            <h2>How can I help?</h2>
                            <p>Ask a question to start a conversation with your local AI model.</p>
                        </div>
                    )}
                    {messages.map((item, index) => (
                        <article className={`message-row ${item.role}`} key={index}>
                            <div className="avatar">{item.role === "user" ? "You" : "AI"}</div>
                            <div className="message-content">
                                <div className="message-author">{item.role === "user" ? "You" : "Assistant"}</div>
                                <div className="message-bubble">{item.content}</div>
                            </div>
                        </article>
                    ))}
                    {busy && (
                        <article className="message-row assistant">
                            <div className="avatar">AI</div>
                            <div className="message-content">
                                <div className="message-author">Assistant</div>
                                <div className="message-bubble typing">Thinking<span> ···</span></div>
                            </div>
                        </article>
                    )}
                    {error && <div className="error-message" role="alert">{error}</div>}
                    <div ref={endRef} />
                </div>

                <form className="composer" onSubmit={sendMessage}>
                    <textarea
                        value={input}
                        onChange={event => setInput(event.target.value)}
                        onKeyDown={event => {
                            if (event.key === "Enter" && !event.shiftKey) {
                                event.preventDefault();
                                sendMessage(event);
                            }
                        }}
                        placeholder="Message the assistant…"
                        rows="1"
                        maxLength="10000"
                        disabled={busy}
                        aria-label="Your message"
                    />
                    <button className="send-button" type="submit" disabled={busy || !input.trim()}>
                        {busy ? "…" : "Send"}
                    </button>
                    <div className="composer-hint">Enter to send · Shift + Enter for a new line</div>
                </form>
            </section>
            <footer>Responses are generated locally by Ollama. Conversation history is held in this browser tab.</footer>
        </main>
    );
}

ReactDOM.createRoot(document.getElementById("root")).render(<ChatApp />);
