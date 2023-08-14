package com.example.demo.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

import javax.transaction.Transactional;

@Transactional
@RestController
@RequestMapping("/api/item")
public class ItemController {

	private final Logger logger = LogManager.getLogger(ItemController.class);

	private ItemRepository itemRepository;

	public ItemController(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		logger.info("getItemById: Start!!!");
		logger.info("getItemById: request items by id = {}", id);
		logger.info("getItemById: Success!");
		logger.info("getItemById: End!");
		return ResponseEntity.of(itemRepository.findById(id));
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		logger.info("getItemsByName: Start!!!");
		logger.info("getItemsByName: request Items by name = {}", name);
		List<Item> items = itemRepository.findByName(name);

		if(CollectionUtils.isEmpty(items)){
			logger.error("getItemsByName: Item not found!");
			logger.info("getItemsByName: End!!!");
			return ResponseEntity.notFound().build();
		}

		logger.info("getItemsByName: Success!");
		logger.info("getItemsByName: End!!!");
		return ResponseEntity.ok(items);
			
	}

	@GetMapping
	public ResponseEntity<List<Item>> getAllItems() {
		logger.info("getAllItems: Start!!!");
		logger.info("getAllItems: Success!");
		logger.info("getAllItems: End!!!");
		return ResponseEntity.ok(itemRepository.findAll());
	}
}
