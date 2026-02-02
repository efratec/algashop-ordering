create table public.tb_shopping_cart (
	id uuid not null,
	created_at timestamp with time zone,
	created_by_user_id uuid,
	last_modified_at timestamp with time zone,
	last_modified_by_user_id uuid,
	total_amount numeric(38,2),
	total_items integer,
	version bigint,
	customer_id uuid not null,
	primary key (id)
);


create index idx_shopping_cart_customer_id on public.tb_shopping_cart (customer_id);
alter table public.tb_shopping_cart add constraint fk_shopping_cart_customer_id foreign key (customer_id) references public.tb_customer(id);


create table public.tb_shopping_cart_item (
	id uuid not null,
	available boolean,
	created_at timestamp with time zone,
	created_by_user_id uuid,
	last_modified_at timestamp with time zone,
	last_modified_by_user_id uuid,
	name varchar(255),
	price numeric(38,2),
	product_id uuid,
	quantity integer,
	total_amount numeric(38,2),
	version bigint,
	shopping_cart_id uuid not null,
	primary key (id)
);

create index idx_shopping_cart_item_shopping_cart_id on public.tb_shopping_cart_item (shopping_cart_id);

alter table public.tb_shopping_cart_item add constraint fk_shopping_cart_item_shopping_cart_id foreign key (shopping_cart_id) references public.tb_shopping_cart(id);