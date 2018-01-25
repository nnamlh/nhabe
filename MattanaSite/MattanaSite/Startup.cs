using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(MattanaSite.Startup))]
namespace MattanaSite
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
        }
    }
}
