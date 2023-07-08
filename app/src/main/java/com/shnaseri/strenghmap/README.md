# SQL Injection
در این پروژه قصد داریم با ابزارهای مختلفی درباره 
SQL Injection
تحقیق کنیم و این ابزارها را در جهت تشخیص و حملات برای وبسایتهای مختلف استفاده کنیم.
در این پروژه از دو نوع مختلف برای حمله استفاده کردیم :
- Query Params
- Forms

در مدل اول، از url استفاده میکنیم و در مدل دوم از فرمهایی که داخل وبسایت استفاده شده است استفاده خواهیم کرد.
برای مثال صفحه زیر را در نظر بگیرید:
    
    http://testphp.vulnweb.com/artists.php?artist=1

میتوانیم یک ریکوئست به سایت بزنیم و خروجی آن را مورد بررسی قرار دهیم.
طبق بررسی های انجام شده چندین کلمه کلیدی وجود دارد، که در صورت پیدا کردن آنها در خروجی ریکوئست، میتوانیم به این پی ببریم که قابل 
Sql Injection
است.
متن زیر بخشی از خروجی ریکوئست است که به کمک آن متوجه میشویم این سایت از لحاظ امنیتی مشکل دارد.

    warning: mysql_fetch_array() expects parameter 1 to be resource, boolean given in /hj/var/www/artists.php on line 62x

در حالت کلی میتوان گفت که رویت هر کدام از موارد زیر در جواب ریکوئست نشانه ی قابلیت ما برای Sql Injection است.
- you have an error in your sql syntax
- warning: mysql
- unclosed quotation mark after the character string
- quoted string not properly terminated

مدل بعدی برای انجام SQL Injection استفاده از فرمهای داخل صفحه وبسایت است.
به این صورت عمل میشود که اگر یک صفحه فرم داشته باشد، میتوان یک دستور SQL نوشت تا با اجرای آن به دیتابیس و جداول تعرض داشته باشیم.
پس برای تشخیص آن ابتدا باید فرمها را کشف کنیم و روی اینکه آن متد کدام مدل است، باید متد را بررسی کنیم که آیا 
get 
است و یا 
post.

     if form_details["method"] == "post":
        res = s.post(url, data=data)
    elif form_details["method"] == "get":
        res = s.get(url, params=data)

سپس نتیجه ریکوئست که در مقدار 
res
ذخیره شده است، مانند مدل اول بررسی شده و میزان
vulnerability
آن را چک میکنیم.
پس از اینکه متوجه شدیم میتوانیم به سایت وارد شویم، میتوانیم فرم را گرفته و روی اینکه هر باکس چه چیزی است، دستور دیتابیس موردنظر را وارد میکنیم.

    [!] Trying http://localhost:8080/DVWA-master/vulnerabilities/sqli/"
    [!] Trying http://localhost:8080/DVWA-master/vulnerabilities/sqli/'
    [+] Detected 1 forms on http://localhost:8080/DVWA-master/vulnerabilities/sqli/.
    [+] SQL Injection vulnerability detected, link: http://localhost:8080/DVWA-master/vulnerabilities/sqli/
    [+] Form:
    {'action': '#',
     'inputs': [{'name': 'id', 'type': 'text', 'value': ''},
            {'name': 'Submit', 'type': 'submit', 'value': 'Submit'}],
     'method': 'get'}