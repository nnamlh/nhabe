using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MattanaSite.Models;
using MattanaSite.Util;
using System.Net;
using System.IO;

namespace MattanaSite.Util
{
    public class Utils
    {
        public static bool send(string user, string title,  string messenge, MongoHelper mongoHelp)
        {
            var firebaseId = mongoHelp.findFirebaseId(user);

            if (firebaseId == "")
            {
                return false;
            }

            title = title.ToUpper();
            string json = "{ \"notification\": {\"click_action\": \"OPEN_ACTIVITY_1\" ,\"title\": \"" + title + "\",\"body\": \"" + messenge + "\"},\"data\": {\"title\": \"'" + title + "'\",\"message\": \"'" + messenge + "'\"},\"to\": \"" + firebaseId + "\"}";

            var responseString = sendRequestFirebase(json);

            if (responseString != "") {
                mongoHelp.saveNoticeHistory(user,title, messenge);
                return true;
            }

            return false;

        }

        public static string sendRequestFirebase(string json)
        {
            string url = @"https://fcm.googleapis.com/fcm/send";

            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);

            request.Method = "POST";
            request.Headers["Authorization"] = "key=AAAAXhRjcMM:APA91bHrXF_RtBRFpGpV3FewUketyEpw11NIXY1lTR-0zBfLjbNarXxwlwx5TH0VJkngsKFXkr61b_pkgWYSt1NSvo170cXBo4TocowSBok-MFQeZvkv171qHk_lFE0l3ox0osSLT_L6";

            System.Text.UTF8Encoding encoding = new System.Text.UTF8Encoding();
            Byte[] byteArray = encoding.GetBytes(json);

            request.ContentLength = byteArray.Length;
            request.ContentType = "application/json";

            using (Stream dataStream = request.GetRequestStream())
            {
                dataStream.Write(byteArray, 0, byteArray.Length);
            }

            long length = 0;

            try
            {
                using (HttpWebResponse response = (HttpWebResponse)request.GetResponse())
                {
                    length = response.ContentLength;
                    var stream = response.GetResponseStream();
                    var reader = new StreamReader(stream, encoding);
                    var responseString = reader.ReadToEnd();

                    return responseString;
                }
            }
            catch
            {
                return "";
            }

        }
    }
}