CREATE TABLE IF NOT EXISTS notification_templates (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    code varchar(100) NOT NULL,
    name varchar(255) NOT NULL,
    channel varchar(50) NOT NULL,
    subject varchar(255),
    body_template TEXT NOT NULL,
    "variables" TEXT ARRAY,
    is_active boolean,
    created_at timestamp(6),
    updated_at timestamp(6)
);

CREATE TABLE IF NOT EXISTS notifications (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id uuid NOT NULL,
    event_type varchar(50),
    template_id uuid,
    channel varchar(50) NOT NULL,
    recipient varchar(255) NOT NULL,
    subject varchar(255),
    body TEXT NOT NULL,
    status varchar(50),
    priority integer DEFAULT 0,
    scheduled_for timestamp(6),
    sent_at timestamp(6),
    delivered_at timestamp(6),
    read_at timestamp(6),
    error_message TEXT,
    retry_count integer DEFAULT 0,
    max_retries integer DEFAULT 5,
    next_attempt timestamp(6),
    provider varchar(100),
    metadata TEXT,
    created_at timestamp(6)
);

CREATE TABLE IF NOT EXISTS notification_queue (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    notification_id uuid NOT NULL,
    provider varchar(100) NOT NULL,
    status varchar(50),
    attempts integer DEFAULT 0,
    last_attempt timestamp(6),
    next_attempt timestamp(6),
    error_message varchar(255),
    created_at timestamp(6)
);

CREATE TABLE IF NOT EXISTS user_preferences (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id uuid NOT NULL UNIQUE,
    email_notifications boolean DEFAULT true,
    sms_notifications boolean DEFAULT true,
    whatsapp_notifications boolean DEFAULT true,
    marketing_opt_in boolean DEFAULT false,
    created_at timestamp(6)
);
