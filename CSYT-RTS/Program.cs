using Newtonsoft.Json.Linq;
using WebSocketSharp;
using WebSocketSharp.Server;

namespace CSYT_RTS
{
    internal class Program : WebSocketBehavior
    {
        static WebSocketServer server;
        static void Main(string[] args)
        {
            server = new WebSocketServer(2232);
            server.AddWebSocketService<Program>("/");
            server.Start();
            while (server.IsListening) { }
        }

        async protected override void OnMessage(MessageEventArgs e)
        {
            Sessions.BroadcastAsync(e.Data, ()=>{});
        }
    }
}