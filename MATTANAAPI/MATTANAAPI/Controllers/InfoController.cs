using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using MATTANAAPI.Models;
using MATTANAAPI.Util;

namespace MATTANAAPI.Controllers
{
    public class InfoController : BaseController
    {


        [HttpGet]
        public List<ProductInfoResult> products()
        {

            var arr = db.MProducts.ToList();

            List<ProductInfoResult> results = new List<ProductInfoResult>();

            foreach (var item in arr)
            {
                results.Add(new ProductInfoResult()
                {
                    id = item.Id,
                    code = item.PCode,
                    describes = item.Describes,
                    name = item.PName,
                    price = item.Price
                });
            }


            return results;

        }

    }
}
