alter table notebook add slug varchar(255) null;
create unique index notebook_slug_uindex on notebook (slug);

alter table category add slug varchar(255) null;
create unique index category_slug_uindex on category (slug);
