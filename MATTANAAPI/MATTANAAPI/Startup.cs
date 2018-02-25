using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(MATTANAAPI.Startup))]
namespace MATTANAAPI
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
           // ConfigureAuth(app);
        }
    }
}
