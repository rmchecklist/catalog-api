-- Core tables
CREATE TABLE IF NOT EXISTS product (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    brand         VARCHAR(255) NOT NULL,
    vendor        VARCHAR(255),
    category      VARCHAR(255) NOT NULL,
    description   TEXT NOT NULL,
    slug          VARCHAR(255) NOT NULL UNIQUE,
    image_url     TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS product_option (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    label       VARCHAR(255) NOT NULL,
    weight      VARCHAR(255),
    min_qty     INTEGER NOT NULL DEFAULT 1,
    available   BOOLEAN NOT NULL DEFAULT TRUE,
    sku         VARCHAR(255),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_product_slug ON product(slug);
CREATE INDEX IF NOT EXISTS idx_product_category ON product(category);
CREATE INDEX IF NOT EXISTS idx_product_brand ON product(brand);
CREATE INDEX IF NOT EXISTS idx_product_option_product ON product_option(product_id);

-- Seed data
INSERT INTO product (name, brand, vendor, category, description, slug, image_url)
VALUES
  ('Stainless Bottle', 'Acme', 'Acme', 'Beverages', 'Insulated stainless steel bottle with multiple weight/size options.', 'stainless-bottle', 'https://images.unsplash.com/photo-1526402462921-9fe5c5f2a2c9?auto=format&fit=crop&w=1200&q=80'),
  ('Eco Mailer', 'Northwind', 'Northwind', 'Packaging', 'Compostable mailer with reinforced seams, ships flat.', 'eco-mailer', 'https://images.unsplash.com/photo-1525909002-1b05e0c869d0?auto=format&fit=crop&w=1200&q=80'),
  ('Carbon Steel Hex Bolt', 'Globex', 'Globex', 'Hardware', 'Industrial grade hex bolt, metric and imperial options.', 'carbon-steel-hex-bolt', 'https://images.unsplash.com/photo-1503389152951-9f343605f61e?auto=format&fit=crop&w=1200&q=80')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO product_option (product_id, label, weight, min_qty, available, sku)
SELECT p.id, o.label, o.weight, o.min_qty, o.available, o.sku
FROM (
  VALUES
    ('stainless-bottle', '500ml', '0.5kg', 25, true, 'ACME-ACME-STAINLESS-BOTTLE-500ML'),
    ('stainless-bottle', '750ml', '0.7kg', 25, true, 'ACME-ACME-STAINLESS-BOTTLE-750ML'),
    ('stainless-bottle', '1L', '1.0kg', 25, false, 'ACME-ACME-STAINLESS-BOTTLE-1L'),
    ('eco-mailer', 'Small', NULL, 100, true, 'NORTHWIND-NORTHWIND-ECO-MAILER-SMALL'),
    ('eco-mailer', 'Medium', NULL, 100, true, 'NORTHWIND-NORTHWIND-ECO-MAILER-MEDIUM'),
    ('eco-mailer', 'Large', NULL, 100, true, 'NORTHWIND-NORTHWIND-ECO-MAILER-LARGE'),
    ('carbon-steel-hex-bolt', 'M8 x 40', NULL, 50, true, 'GLOBEX-GLOBEX-CARBON-STEEL-HEX-BOLT-M8X40'),
    ('carbon-steel-hex-bolt', 'M10 x 50', NULL, 50, true, 'GLOBEX-GLOBEX-CARBON-STEEL-HEX-BOLT-M10X50'),
    ('carbon-steel-hex-bolt', '3/8in x 2in', NULL, 50, false, 'GLOBEX-GLOBEX-CARBON-STEEL-HEX-BOLT-3-8INX2IN')
) AS o(slug, label, weight, min_qty, available, sku)
JOIN product p ON p.slug = o.slug
ON CONFLICT DO NOTHING;
