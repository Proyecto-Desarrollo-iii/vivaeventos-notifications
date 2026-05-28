INSERT INTO notification_templates (id, code, name, channel, subject, body_template, variables, is_active, created_at, updated_at) VALUES
(
    gen_random_uuid(),
    'PURCHASE_EMAIL',
    'Confirmación de compra',
    'EMAIL',
    '¡Gracias por tu compra, {{nombre}}!',
    '<!DOCTYPE html><html><body style="font-family:Arial,sans-serif;padding:20px;max-width:600px;margin:auto;">
        <h2 style="color:#2d89ef;">✅ Compra confirmada</h2>
        <p>Hola <strong>{{nombre}}</strong>,</p>
        <p>Tu compra para <strong>{{evento}}</strong> se ha realizado con éxito.</p>
        <p><strong>Fecha:</strong> {{fecha}}<br>
        <strong>Cantidad:</strong> {{cantidad}} entrada(s)<br>
        <strong>Total:</strong> ${{total}}</p>
        <p>Presenta este código QR en la entrada: <strong>{{codigo_qr}}</strong></p>
        <hr><p style="color:#888;">¡Disfruta el evento!</p>
    </body></html>',
    ARRAY['nombre', 'evento', 'fecha', 'cantidad', 'total', 'codigo_qr'],
    true,
    NOW(), NOW()
),
(
    gen_random_uuid(),
    'REMINDER_EMAIL',
    'Recordatorio de evento',
    'EMAIL',
    '📅 Recordatorio: {{evento}} es mañana',
    '<!DOCTYPE html><html><body style="font-family:Arial,sans-serif;padding:20px;max-width:600px;margin:auto;">
        <h2 style="color:#f39c12;">⏰ Recordatorio</h2>
        <p>Hola <strong>{{nombre}}</strong>,</p>
        <p>Te recordamos que <strong>{{evento}}</strong> es mañana <strong>{{fecha}}</strong>.</p>
        <p><strong>Lugar:</strong> {{lugar}}<br>
        <strong>Hora:</strong> {{hora}}</p>
        <p>No olvides llevar tu entrada con código <strong>{{codigo_qr}}</strong>.</p>
        <hr><p style="color:#888;">¡Te esperamos!</p>
    </body></html>',
    ARRAY['nombre', 'evento', 'fecha', 'lugar', 'hora', 'codigo_qr'],
    true,
    NOW(), NOW()
),
(
    gen_random_uuid(),
    'CHANGE_EMAIL',
    'Cambio en evento',
    'EMAIL',
    '🔔 Cambios en {{evento}}',
    '<!DOCTYPE html><html><body style="font-family:Arial,sans-serif;padding:20px;max-width:600px;margin:auto;">
        <h2 style="color:#e67e22;">🔄 Cambio en tu evento</h2>
        <p>Hola <strong>{{nombre}}</strong>,</p>
        <p>Te informamos que se han realizado los siguientes cambios en <strong>{{evento}}</strong>:</p>
        <p><strong>Cambio:</strong> {{detalle_cambio}}</p>
        <p><strong>Nueva fecha:</strong> {{nueva_fecha}}<br>
        <strong>Nuevo lugar:</strong> {{nuevo_lugar}}</p>
        <p>Disculpa las molestias. Puedes ver los detalles actualizados en tu panel.</p>
        <hr><p style="color:#888;">Gracias por tu comprensión</p>
    </body></html>',
    ARRAY['nombre', 'evento', 'detalle_cambio', 'nueva_fecha', 'nuevo_lugar'],
    true,
    NOW(), NOW()
),
(
    gen_random_uuid(),
    'CANCELLATION_EMAIL',
    'Cancelación de evento',
    'EMAIL',
    '❌ Evento cancelado: {{evento}}',
    '<!DOCTYPE html><html><body style="font-family:Arial,sans-serif;padding:20px;max-width:600px;margin:auto;">
        <h2 style="color:#e74c3c;">❌ Evento cancelado</h2>
        <p>Hola <strong>{{nombre}}</strong>,</p>
        <p>Lamentamos informarte que <strong>{{evento}}</strong> programado para el {{fecha}} ha sido cancelado.</p>
        <p><strong>Motivo:</strong> {{motivo}}</p>
        <p>El reembolso de <strong>${{total}}</strong> será procesado a tu método de pago en un plazo de 5-7 días hábiles.</p>
        <p>Si tienes dudas, contáctanos.</p>
        <hr><p style="color:#888;">Sentimos las molestias</p>
    </body></html>',
    ARRAY['nombre', 'evento', 'fecha', 'motivo', 'total'],
    true,
    NOW(), NOW()
),
(
    gen_random_uuid(),
    'PROMOTION_EMAIL',
    'Oferta especial para ti',
    'EMAIL',
    '🎉 {{descuento}} off en {{evento}}',
    '<!DOCTYPE html><html><body style="font-family:Arial,sans-serif;padding:20px;max-width:600px;margin:auto;">
        <h2 style="color:#9b59b6;">🎉 Oferta especial</h2>
        <p>Hola <strong>{{nombre}}</strong>,</p>
        <p>No te pierdas <strong>{{evento}}</strong> — tenemos una oferta especial para ti:</p>
        <p style="font-size:24px;text-align:center;padding:15px;background:#f0f0f0;border-radius:8px;">
            <strong>{{descuento}}</strong>
        </p>
        <p><strong>Código:</strong> {{codigo_promocion}}<br>
        <strong>Válido hasta:</strong> {{fecha_expiracion}}</p>
        <p>Usa el código al comprar en vivaeventos.com</p>
        <hr><p style="color:#888;">No dejes pasar esta oportunidad</p>
    </body></html>',
    ARRAY['nombre', 'evento', 'descuento', 'codigo_promocion', 'fecha_expiracion'],
    true,
    NOW(), NOW()
);
