-- artists definition

CREATE TABLE "artists"
(
    [ArtistId] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    [Name] NVARCHAR(120)
);


-- customers definition

CREATE TABLE customers (
	CustomerId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	FirstName NVARCHAR NOT NULL,
	LastName NVARCHAR NOT NULL,
	Company NVARCHAR,
	Address NVARCHAR,
	City NVARCHAR,
	State NVARCHAR,
	Country NVARCHAR,
	PostalCode NVARCHAR,
	Phone NVARCHAR,
	Fax NVARCHAR,
	Email NVARCHAR NOT NULL
);


-- genres definition

CREATE TABLE "genres"
(
    [GenreId] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    [Name] NVARCHAR(120)
);


-- media_types definition

CREATE TABLE "media_types"
(
    [MediaTypeId] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    [Name] NVARCHAR(120)
);


-- albums definition

CREATE TABLE "albums"
(
    [AlbumId] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    [Title] NVARCHAR(160)  NOT NULL,
    [ArtistId] INTEGER  NOT NULL,
    FOREIGN KEY ([ArtistId]) REFERENCES "artists" ([ArtistId]) 
		ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX [IFK_AlbumArtistId] ON "albums" ([ArtistId]);


-- app_users definition

CREATE TABLE app_users (
	UserId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	Username TEXT NOT NULL,
	Password TEXT NOT NULL,
	"Role" TEXT,
	CONSTRAINT app_users_customers_FK FOREIGN KEY (UserId) REFERENCES customers(CustomerId)
);


-- cart definition

CREATE TABLE cart (
	CartId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	CustomerId INTEGER NOT NULL,
	CartDate DATETIME NOT NULL,
	BillingAddress NVARCHAR,
	BillingCity NVARCHAR,
	BillingState NVARCHAR,
	BillingCountry NVARCHAR,
	BillingPostalCode NVARCHAR,
	Total NUMERIC NOT NULL,
	CONSTRAINT FK_cart_customers FOREIGN KEY (CustomerId) REFERENCES customers(CustomerId)
);


-- invoices definition

CREATE TABLE "invoices"
(
    [InvoiceId] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    [CustomerId] INTEGER  NOT NULL,
    [InvoiceDate] DATETIME  NOT NULL,
    [BillingAddress] NVARCHAR(70),
    [BillingCity] NVARCHAR(40),
    [BillingState] NVARCHAR(40),
    [BillingCountry] NVARCHAR(40),
    [BillingPostalCode] NVARCHAR(10),
    [Total] NUMERIC(10,2)  NOT NULL,
    FOREIGN KEY ([CustomerId]) REFERENCES "customers" ([CustomerId]) 
		ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX [IFK_InvoiceCustomerId] ON "invoices" ([CustomerId]);


-- tracks definition

CREATE TABLE "tracks"
(
    [TrackId] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    [Name] NVARCHAR(200)  NOT NULL,
    [AlbumId] INTEGER,
    [MediaTypeId] INTEGER  NOT NULL,
    [GenreId] INTEGER,
    [Composer] NVARCHAR(220),
    [Milliseconds] INTEGER  NOT NULL,
    [Bytes] INTEGER,
    [UnitPrice] NUMERIC(10,2)  NOT NULL,
    FOREIGN KEY ([AlbumId]) REFERENCES "albums" ([AlbumId]) 
		ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY ([GenreId]) REFERENCES "genres" ([GenreId]) 
		ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY ([MediaTypeId]) REFERENCES "media_types" ([MediaTypeId]) 
		ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX [IFK_TrackAlbumId] ON "tracks" ([AlbumId]);
CREATE INDEX [IFK_TrackGenreId] ON "tracks" ([GenreId]);
CREATE INDEX [IFK_TrackMediaTypeId] ON "tracks" ([MediaTypeId]);


-- cart_items definition

CREATE TABLE "cart_items"
(
    [CartLineId] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    [CartId] INTEGER  NOT NULL,
    [TrackId] INTEGER  NOT NULL,
    [UnitPrice] NUMERIC(10,2)  NOT NULL,
    [Quantity] INTEGER  NOT NULL,
    FOREIGN KEY ([CartId]) REFERENCES "cart" ([CartId]) 
		ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY ([TrackId]) REFERENCES "tracks" ([TrackId]) 
		ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX [IFK_CartLineCartId] ON "cart_items" ([CartId]);
CREATE INDEX [IFK_CartLineTrackId] ON "cart_items" ([TrackId]);


-- invoice_items definition

CREATE TABLE "invoice_items"
(
    [InvoiceLineId] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    [InvoiceId] INTEGER  NOT NULL,
    [TrackId] INTEGER  NOT NULL,
    [UnitPrice] NUMERIC(10,2)  NOT NULL,
    [Quantity] INTEGER  NOT NULL,
    FOREIGN KEY ([InvoiceId]) REFERENCES "invoices" ([InvoiceId]) 
		ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY ([TrackId]) REFERENCES "tracks" ([TrackId]) 
		ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX [IFK_InvoiceLineInvoiceId] ON "invoice_items" ([InvoiceId]);
CREATE INDEX [IFK_InvoiceLineTrackId] ON "invoice_items" ([TrackId]);