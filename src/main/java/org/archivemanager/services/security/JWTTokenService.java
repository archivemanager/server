package org.archivemanager.services.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;

import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.services.date.DateService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

import java.util.Date;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.TextCodec.BASE64;

import static org.apache.commons.lang3.StringUtils.substringBeforeLast;


@Service
public class JWTTokenService implements Clock, TokenService {
	private static final String DOT = ".";
	private static final GzipCompressionCodec COMPRESSION_CODEC = new GzipCompressionCodec();
	@Autowired private DateService dateService;
	@Autowired private PropertyConfiguration propertyConfig;
	
	
	@Override
	public String permanent(final Map<String, String> attributes) {
		return newToken(attributes, 0);
	}

	@Override
	public String expiring(final Map<String, String> attributes) {
		return newToken(attributes, propertyConfig.getTokenExpiration());
	}

	private String newToken(final Map<String, String> attributes, final int expiresInSec) {
		final DateTime now = dateService.now();
		final Claims claims = Jwts.claims().setIssuer(propertyConfig.getTokenIssuer()).setIssuedAt(now.toDate());
		if(expiresInSec > 0) {
			final DateTime expiresAt = now.plusSeconds(expiresInSec);
			claims.setExpiration(expiresAt.toDate());
		}
		claims.putAll(attributes);

		return Jwts.builder().setClaims(claims).signWith(HS256, BASE64.encode(propertyConfig.getTokenSecretKey())).compressWith(COMPRESSION_CODEC).compact();
	}

	@Override
	public Map<String, String> verify(final String token) {
		final JwtParser parser = Jwts.parser().requireIssuer(propertyConfig.getTokenIssuer()).setClock(this).setAllowedClockSkewSeconds(propertyConfig.getTokenClockSkew())
				.setSigningKey(BASE64.encode(propertyConfig.getTokenSecretKey()));
		return parseClaims(() -> parser.parseClaimsJws(token).getBody());
	}

	@Override
	public Map<String, String> untrusted(final String token) {
		final JwtParser parser = Jwts.parser().requireIssuer(propertyConfig.getTokenIssuer()).setClock(this).setAllowedClockSkewSeconds(propertyConfig.getTokenClockSkew());
		// See: https://github.com/jwtk/jjwt/issues/135
		final String withoutSignature = substringBeforeLast(token, DOT) + DOT;
		return parseClaims(() -> parser.parseClaimsJwt(withoutSignature).getBody());
	}

	private static Map<String, String> parseClaims(final Supplier<Claims> toClaims) {
		try {
			final Claims claims = toClaims.get();
			final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
			for (final Map.Entry<String, Object> e: claims.entrySet()) {
				builder.put(e.getKey(), String.valueOf(e.getValue()));
			}
			return builder.build();
	    } catch (final IllegalArgumentException | JwtException e) {
	      return ImmutableMap.of();
	    }
	}

	@Override
	public Date now() {
		final DateTime now = dateService.now();
	    return now.toDate();
	}
}