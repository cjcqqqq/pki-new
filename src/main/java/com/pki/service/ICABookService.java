package com.pki.service;


import com.pki.entity.Cabook;

import java.util.List;

public interface ICABookService {
   public void Save(Cabook book);

   public void updata(Cabook book);

   public List<Cabook>getBookByStart(String start);

   public Long getBookByStartLong(int start);

   public List<Cabook> getBookByUId(int Uid, String start);
   
   public Long getBookByUIdLong(int start);
   
   public void delete(Cabook bookCabook);
  	
}
