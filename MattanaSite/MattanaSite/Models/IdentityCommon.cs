using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MattanaSite.Models
{
    public class IdentityCommon
    {
    }

    public class SubMenuInfo
    {
        public string Name { get; set; }

        public string Url { get; set; }

        public int Active { get; set; }
    }


    public class LocationInfo
    {
        public double lat { get; set; }

        public double lng { get; set; }

    }

}