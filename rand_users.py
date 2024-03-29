import hashlib,hmac,random,string
from io import StringIO
from typing import AnyStr
DEFAULT_PSWD = b"Admin135"
EMPTY_MD5 = hashlib.md5()
def get_pswd(pswd:AnyStr,role:AnyStr):
    if isinstance(pswd,str):
        pswd = pswd.encode("latin-1")
    if isinstance(role,str):
        role = role.encode("utf-8")
    MD5 = hashlib.md5()
    MD5.update(pswd)
    pswd_md5_1 = MD5.digest() #第一次哈希结果
    MD5 = EMPTY_MD5.copy() #清空
    MD5.update(pswd_md5_1)
    # print(MD5.hexdigest())
    hmac256=hmac.HMAC(key=role,digestmod=hashlib.sha256)
    hmac256.update(MD5.hexdigest().encode("latin-1"))
    return hmac256.hexdigest()
collection = string.ascii_lowercase+string.digits+'_'
def get_rand_uname():
    '''生成长度[5,10)的用户名'''
    global collection
    _len = random.randrange(5,10)
    uname = StringIO()
    uname.write(random.choice(string.ascii_lowercase))
    for i in range(0,_len-1):
        uname.write(random.choice(collection))
    return uname.getvalue()
def getRandomNameWithGender():
    family_names = "赵钱孙李周吴郑王"
    names_man = "伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山全国胜学祥才发武新利清"
    names_woman= "雪荣爱霞香月莺媛艳瑞凡佳嘉琼勤珍瑾颖露瑶慧巧美娜静"
    len_ = random.randint(1,2)
    gender = random.randint(0,1)
    name = random.choice(family_names)
    for _ in range(0,len_):
        name += random.choice(names_man if gender == 0 else names_woman)
    return "男" if gender == 0 else "女",name
def getRandomPhone():
    _phone = int(1e10)+random.randrange(int(3e9),int(1e10))
    return str(_phone)
def generate(len_:int):
    info = StringIO(newline='\n')
    for _ in range(0,len_):
        uname = get_rand_uname()
        gender,name = getRandomNameWithGender()
        phone = getRandomPhone()
        role = random.choice((1,2,3))
        if role == 3:role = 2 #提高学生比例
        pswd = get_pswd("Admin135" if role == 1 else "Student1",str(role))
        info.write("INSERT INTO `USERS`(`USERNAME`,`PASSWORD`,`TRUENAME`,`GENDER`,PHONE,ROLE) VALUES('{}','{}','{}','{}','{}',{})\n"
                   .format(uname,pswd,name,gender,phone,role))
    val = info.getvalue()
    info.close()
    return val
if __name__ == "__main__":
    fp = open("users.sql","w",encoding="utf-8")
    fp.write(generate(30))
    fp.close()