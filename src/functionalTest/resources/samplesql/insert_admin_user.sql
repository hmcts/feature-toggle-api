insert into users(username,password,enabled)
values('cmcadmin@hmcts.net','$2a$10$rvPPsFi6BQ77uRkFDwfZ8.bF3QUG2fKEKPk4wkUs/IIgq3fFnb3wW',true);

insert into authorities(username,authority)
values('cmcadmin@hmcts.net','ROLE_ADMIN');
