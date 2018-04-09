using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using MATTANAAPI.Models;
using MATTANAAPI.Util;
using System.Web.Script.Serialization;
using System.Data.Entity;

namespace MATTANAAPI.Controllers
{
    public class OrderController : BaseController
    {

        [HttpPost]
        public ResultInfo CreateOrder()
        {

            var log = new MongoHistoryAPI()
        {
            APIUrl = "/api/order/createorder",
            CreateTime = DateTime.Now,
            Sucess = 1
        };

            var result = new ResultInfo()
            {
                id = "1",
                msg = "success"
            };

            try
            {
                var requestContent = Request.Content.ReadAsStringAsync().Result;
                var jsonserializer = new JavaScriptSerializer();
                var paser = jsonserializer.Deserialize<CreateOrderRequest>(requestContent);
                log.Content = new JavaScriptSerializer().Serialize(paser);

                if (!mongoHelper.checkLoginSession(paser.user, paser.token))
                    throw new Exception("Wrong token and user login!");


                var checkStaff = db.MStaffs.Where(p => p.MUser == paser.user).FirstOrDefault();

                if (checkStaff == null)
                    throw new Exception("Sai thông tin");

                var checkAgency = db.MAgencies.Where(p => p.Code == paser.agencyId).FirstOrDefault();

                if (checkAgency == null)
                    throw new Exception("Sai thông tin");

                var newCode = GetCode();

                Nullable<System.DateTime> sugestTime = null;

                try
                {
                    sugestTime = DateTime.ParseExact(paser.suggestDate, "d/M/yyyy", null);
                }
                catch
                {

                }

                var order = new MOrder()
                {
                    Id = Guid.NewGuid().ToString(),
                    CreateTime = DateTime.Now,
                    AgencyId = checkAgency.Id,
                    CloseOrder = 0,
                    ModifyTime = DateTime.Now,
                    PriceReal = 0,
                    StaffId = checkStaff.Id,
                    StatusId = "create",
                    Code = newCode,
                    SuggestDate = sugestTime
                };

                db.MOrders.Add(order);
                db.SaveChanges();

                double? priceTotal = 0;

                foreach (var item in paser.products)
                {
                    var checkProduct = db.MProducts.Find(item.id);
                    if (checkProduct != null)
                    {
                        var price = checkProduct.Price * item.quantity;

                        priceTotal += price;

                        var productOder = new ProductOrder()
                        {
                            OrderId = order.Id,
                            Price = checkProduct.Price,
                            ProductId = checkProduct.Id,
                            QuantityBuy = item.quantity,
                            QuantityReal = item.quantity
                        };
                        db.ProductOrders.Add(productOder);
                        db.SaveChanges();
                    }
                }


                order.PriceOrder = priceTotal;
                order.PriceReal = priceTotal;
                db.Entry(order).State = System.Data.Entity.EntityState.Modified;
                db.SaveChanges();

            }
            catch (Exception e)
            {
                result.id = "0";
                result.msg = e.Message;
                log.Sucess = 0;
            }

            log.ReturnInfo = new JavaScriptSerializer().Serialize(result);

            mongoHelper.createHistoryAPI(log);

            return result;


        }

        private string GetCode()
        {
            var charId = db.OrderNumbers.Max(p => p.CharId);

            if (charId == null)
            {
                charId = 65;
            }

            var number = db.OrderNumbers.Where(p => p.CharId == charId).Max(p => p.Number);

            if (number == null)
                number = 10000;
            else
            {
                if (number == 99999)
                {
                    charId++;
                    number = 10000;
                }
                else
                {
                    number++;
                }
            }

            char character = (char)charId;
            string code = character.ToString() + number;

            var save = new OrderNumber()
            {
                Id = code,
                CharId = charId,
                Number = number
            };
            db.OrderNumbers.Add(save);
            db.SaveChanges();

            return code;

        }

        // show order
        [HttpGet]
        public ShowOrderResult ShowOrder(string user, string fDate, string tDate, string status, string agency)
        {
            var history = new MongoHistoryAPI()
             {
                 APIUrl = "/api/order/showorder",
                 CreateTime = DateTime.Now,
                 Sucess = 1
             };

            var result = new ShowOrderResult()
             {
                 id = "1",
                 msg = "success",
                 orders = new List<ShowOrderInfo>()
             };

            try
            {
                var checkUser = db.MStaffs.Where(p => p.MUser == user).FirstOrDefault();

                if (checkUser == null)
                    throw new Exception("Sai thông tin");

                DateTime fromDate = DateTime.ParseExact(fDate, "d/M/yyyy", null);

                DateTime toDate = DateTime.ParseExact(tDate, "d/M/yyyy", null);

                if (status == "all")
                    status = "";

                if (String.IsNullOrEmpty(agency))
                    agency = "";



                var orders = new List<MOrder>();

                if (isAdmin(user))
                {
                    orders= (from log in db.MOrders
                     where DbFunctions.TruncateTime(log.CreateTime)
                                        >= DbFunctions.TruncateTime(fromDate) && DbFunctions.TruncateTime(log.CreateTime)
                                        <= DbFunctions.TruncateTime(toDate) && log.StatusId.Contains(status) && log.MAgency.Code.Contains(agency)

                     select log).OrderByDescending(p => p.CreateTime).ToList();
                }
                else
                {
                    orders = (from log in db.MOrders
                              where DbFunctions.TruncateTime(log.CreateTime)
                                                 >= DbFunctions.TruncateTime(fromDate) && DbFunctions.TruncateTime(log.CreateTime)
                                                 <= DbFunctions.TruncateTime(toDate) && log.StatusId.Contains(status) && log.StaffId == checkUser.Id && log.MAgency.Code.Contains(agency)
                              select log).OrderByDescending(p => p.CreateTime).ToList();
                }

                //var orders = db.MOrders.Where(p => p.StaffId == checkUser.Id).OrderByDescending(p=> p.CreateTime).ToList();

                foreach (var item in orders)
                {
                    var order = new ShowOrderInfo()
                    {
                        code = item.Code,
                        orderPrice = item.PriceOrder.Value.ToString("C", Cultures.VietNam),
                        productNumber = item.ProductOrders.Count(),
                        agency = item.MAgency.Code,
                        status = item.OrderStatu.Name,
                        statusCode = item.OrderStatu.Id,
                        store = item.MAgency.Store,
                        phone = item.MAgency.Phone,
                        address = item.MAgency.AddressDetail,
                        close = (int)item.CloseOrder,
                        orderId = item.Id,
                        createTime = item.CreateTime.Value.ToString("dd/MM/yyyy"),
                        timeSuggest = item.SuggestDate != null?item.SuggestDate.Value.ToString("dd/MM/yyyy"):"",
                        realPrice = item.PriceReal.Value.ToString("C", Cultures.VietNam),
                        staffCode = item.StaffId,
                        staffName = item.MStaff.FullName
                    };

                    var nextStt = db.OrderStatus.Where(p => p.PreStt == item.StatusId).FirstOrDefault();

                    if (nextStt != null)
                    {
                        order.nextStatus = nextStt.Name;
                        order.nextStatusCode = nextStt.Id;
                    }

                    result.orders.Add(order);
                }

            }
            catch (Exception e)
            {
                result.id = "0";
                result.msg = e.Message;
                history.Sucess = 0;
            }

            history.ReturnInfo = new JavaScriptSerializer().Serialize(result);

            mongoHelper.createHistoryAPI(history);

            return result;
        }

        [HttpGet]
        public List<ShowProductOrderInfo> ShowProductOrder(string orderId)
        {

            var log = new MongoHistoryAPI()
            {
                APIUrl = "/api/order/showproductorder",
                CreateTime = DateTime.Now,
                Sucess = 1
            };

            var result = new List<ShowProductOrderInfo>();

            try
            {
                var checkOrder = db.MOrders.Find(orderId);
                if (checkOrder == null)
                    throw new Exception("Sai thông tin");

                foreach (var item in checkOrder.ProductOrders)
                {
                    result.Add(new ShowProductOrderInfo()
                    {
                        code = item.MProduct.PCode,
                        name = item.MProduct.PName,
                        price = item.Price.Value.ToString("C", Cultures.VietNam),
                        priceTotal = (item.QuantityBuy * item.Price).Value.ToString("C", Cultures.VietNam),
                        quantityBuy = (int)item.QuantityBuy,
                        quantityReal = (int)item.QuantityReal,
                        Id = item.ProductId
                    });
                }

            }
            catch (Exception e)
            {
                log.Sucess = 0;
            }

            log.ReturnInfo = new JavaScriptSerializer().Serialize(result);

            mongoHelper.createHistoryAPI(log);

            return result;

        }


        [HttpGet]
        public UpdateOrderResult UpdateOrderStatus(string user, string token, string orderId, string status)
        {
             var log = new MongoHistoryAPI()
            {
                APIUrl = "/api/order/updateorderstatus",
                CreateTime = DateTime.Now,
                Sucess = 1
            };

             var result = new UpdateOrderResult()
             {
                 id = "1",
                 msg = "success"
             };

            try
            {
                if (!mongoHelper.checkLoginSession(user, token))
                    throw new Exception("Wrong token and user login!");

                var checkStaff = db.MStaffs.Where(p => p.MUser == user).FirstOrDefault();

                if (checkStaff == null)
                    throw new Exception("Sai thông tin nv");

                if (!isAdmin(user))
                    throw new Exception("Sai thông tin quyen");

                var checkStt = db.OrderStatus.Find(status);

                if(checkStt == null)
                    throw new Exception("Sai thông tin status");

                var checkOrder = db.MOrders.Find(orderId);

                if (checkOrder == null)
                    throw new Exception("Sai thông tin order");

                if (checkOrder.StatusId == checkStt.Id)
                    throw new Exception("Trang thai khong khớp");

                if (checkStt.PreStt != checkOrder.StatusId)
                    throw new Exception("Sai trạng thái");

                checkOrder.StatusId = checkStt.Id;
                db.Entry(checkOrder).State = EntityState.Modified;
                db.SaveChanges();

                Utils.send(checkOrder.MStaff.MUser, "Đơn hàng", "Đơn hàng " + checkOrder.Code + " \nCủa đại lý: " + checkOrder.MAgency.Store + " \nĐã thay đổi trạng thái: " + checkStt.Name, mongoHelper);

                result.status = checkStt.Name;
                result.statusCode = checkStt.Id;

                var findNextStt = db.OrderStatus.Where(p => p.PreStt == checkStt.Id).FirstOrDefault();

                if (findNextStt != null)
                {
                    result.nextStatus = findNextStt.Name;
                    result.nextStatusCode = findNextStt.Id;
                }
            
            }
            catch (Exception e)
            {
                log.Sucess = 0;
                result.id = "0";
                result.msg = e.Message;

            }

            log.ReturnInfo = new JavaScriptSerializer().Serialize(result);

            mongoHelper.createHistoryAPI(log);

            return result;
        }

        [HttpGet]
        public ResultInfo UpdateDelivery(string orderId, string productId, int quantity, string user, string token)
        {
             var log = new MongoHistoryAPI()
            {
                APIUrl = "/api/order/updatedelivery",
                CreateTime = DateTime.Now,
                Sucess = 1
            };

             var result = new ResultInfo()
             {
                 id = "1",
                 msg = "success"
             };

            try
            {
                if (!mongoHelper.checkLoginSession(user, token))
                    throw new Exception("Wrong token and user login!");

                if (!isAdmin(user))
                    throw new Exception("Sai thông tin");

                var checkOrder = db.MOrders.Find(orderId);
                if (checkOrder == null)
                    throw new Exception("Sai thông tin");

                if (checkOrder.StatusId == "create")
                {
                    var oderProduct = checkOrder.ProductOrders.Where(p => p.ProductId == productId).FirstOrDefault();

                    if (oderProduct == null)
                        throw new Exception("Sai thông tin");

                    oderProduct.QuantityReal = quantity;
                    db.Entry(oderProduct).State = EntityState.Modified;
                    db.SaveChanges();

                    double? total = 0;

                    foreach (var item in checkOrder.ProductOrders)
                    {
                        total += (item.QuantityReal * item.Price);
                    }

                    checkOrder.PriceReal = total;
                    db.Entry(checkOrder).State = EntityState.Modified;
                    db.SaveChanges();

                    Util.Utils.send(checkOrder.MStaff.MUser, "Đơn hàng " + checkOrder.Code, "Đơn hàng " + checkOrder.Code + "\nĐã thay đổi số lượng thực: " + quantity + "\nSản phẩm: " + oderProduct.MProduct.PName, mongoHelper);


                } else
                    throw new Exception("Sai thông tin");

            }
            catch (Exception e)
            {
                log.Sucess = 0;
            }

            log.ReturnInfo = new JavaScriptSerializer().Serialize(result);

            mongoHelper.createHistoryAPI(log);

            return result;
        }
    }
}
