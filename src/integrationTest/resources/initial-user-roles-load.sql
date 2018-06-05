insert into users(username,password,enabled)
values('admintest@hmcts.net','$2a$10$Ssplo0gK3TlhtVjIFLodse1BvIDi5CinN9aSxHuyEqfxIEtBESkdS',true);

insert into authorities(username,authority)
values('admintest@hmcts.net','ROLE_ADMIN');

insert into users(username,password,enabled)
values('editortest@hmcts.net','$2a$10$I9yVIDI5xh70w1gmO5FgkO6SIsswQl8.vLxClRWMbZ4jgvSwyg2YO',true);

insert into authorities(username,authority)
values('editortest@hmcts.net','ROLE_EDITOR');

insert into users(username,password,enabled)
values('readtest@hmcts.net','$2a$10$l2k5Mc3vsLJVEV.TfdzBJuisCZM.OPWnVNN6X2cF7S4P6bMdsA89K',true);

insert into authorities(username,authority)
values('readtest@hmcts.net','ROLE_USER');
