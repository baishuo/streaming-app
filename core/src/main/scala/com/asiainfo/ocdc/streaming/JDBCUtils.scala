package com.asiainfo.ocdc.streaming

import java.sql.{Statement, ResultSet, DriverManager, Connection}

import scala.collection.mutable.ArrayBuffer

/**
 * Created by tianyi on 3/30/15.
 */
object JDBCUtils {

  private def connection: Connection = {
    val conn_str = "jdbc:mysql://localhost:3306/streaming?user=root&password=mz8xvw7+MI"
    classOf[com.mysql.jdbc.Driver]
    DriverManager.getConnection(conn_str,"root","mz8xvw7+MI")
  }

  def query(sql: String): Array[Map[String, String]] = {
    var statement: Statement = null
    var rs: ResultSet = null
    var result = ArrayBuffer[Map[String, String]]()
    try {
      // Configure to be Read Only
      statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)

      // Execute Query
      rs = statement.executeQuery(sql)

      // Getting column names
      val md = rs.getMetaData

      val columnNames = (1 to md.getColumnCount).map(md.getColumnName(_)).toList


      // Iterate Over ResultSet
      while (rs.next) {
        val line: Map[String, String] = (1 to md.getColumnCount).map(index =>{
          (md.getColumnName(index), rs.getString(index))
        }).toMap[String, String]
        result += line
      }
      result.toArray
    }
    finally {
      if(statement != null) statement.close()
      if(rs != null) rs.close()
    }
  }

  def execute(sql: String): Unit = {
    var statement: Statement = null
    try {
      // Configure to be Read Only
      statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)

      // Execute Query
      statement.execute(sql)
    }
    finally {
      if(statement != null) statement.close()
    }
  }

  def main(args: Array[String]) {
    JDBCUtils.query("select * from EVENT_RULE").map(line => {
      line.keySet.map(key => key + "=" + line(key))
    }).map(println)
  }
}
