using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MATTANAAPI.Models
{
    public class IdentityLogin
    {
    }

    public class LoginResult : ResultInfo
    {

        public string token { get; set; }

        public string user { get; set; }


    }
}