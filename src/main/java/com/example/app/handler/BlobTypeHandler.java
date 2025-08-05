    package com.example.app.handler;

    import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

    public class BlobTypeHandler extends BaseTypeHandler<byte[]> {

        // DBからデータ取得時
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, byte[] parameter, JdbcType jdbcType) throws SQLException {
            ByteArrayInputStream bis = new ByteArrayInputStream(parameter);
            ps.setBlob(i, bis, parameter.length);
        }

        // DBからデータ取得時 (カラム名)
        @Override
        public byte[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
            Blob blob = rs.getBlob(columnName);
            if (blob == null) {
                return null;
            }
            return toByteArray(blob);
        }

        // DBからデータ取得時 (カラムインデックス)
        @Override
        public byte[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
            Blob blob = rs.getBlob(columnIndex);
            if (blob == null) {
                return null;
            }
            return toByteArray(blob);
        }

        // DBからデータ取得時 (CallableStatement)
        @Override
        public byte[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
            Blob blob = cs.getBlob(columnIndex);
            if (blob == null) {
                return null;
            }
            return toByteArray(blob);
        }

        private byte[] toByteArray(Blob blob) throws SQLException {
            try {
                return blob.getBytes(1, (int) blob.length());
            } finally {
                try {
                    if (blob != null) {
                        blob.free();
                    }
                } catch (AbstractMethodError e) {
                    //一部のJDBCドライバーでfree()が実装されていない場合があるため無視する
                }
            }
        }
    }
    
